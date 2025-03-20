<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="ventanillaUnicaApp.pqrs.home.createOrEditLabel"
          data-cy="PqrsCreateUpdateHeading"
          v-text="t$('ventanillaUnicaApp.pqrs.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="pqrs.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="pqrs.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('ventanillaUnicaApp.pqrs.titulo')" for="pqrs-titulo"></label>
            <input
              type="text"
              class="form-control"
              name="titulo"
              id="pqrs-titulo"
              data-cy="titulo"
              :class="{ valid: !v$.titulo.$invalid, invalid: v$.titulo.$invalid }"
              v-model="v$.titulo.$model"
              required
            />
            <div v-if="v$.titulo.$anyDirty && v$.titulo.$invalid">
              <small class="form-text text-danger" v-for="error of v$.titulo.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('ventanillaUnicaApp.pqrs.descripcion')" for="pqrs-descripcion"></label>
            <textarea
              class="form-control"
              name="descripcion"
              id="pqrs-descripcion"
              data-cy="descripcion"
              :class="{ valid: !v$.descripcion.$invalid, invalid: v$.descripcion.$invalid }"
              v-model="v$.descripcion.$model"
              required
            ></textarea>
            <div v-if="v$.descripcion.$anyDirty && v$.descripcion.$invalid">
              <small class="form-text text-danger" v-for="error of v$.descripcion.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('ventanillaUnicaApp.pqrs.fechaCreacion')" for="pqrs-fechaCreacion"></label>
            <div class="d-flex">
              <input
                id="pqrs-fechaCreacion"
                data-cy="fechaCreacion"
                type="datetime-local"
                class="form-control"
                name="fechaCreacion"
                :class="{ valid: !v$.fechaCreacion.$invalid, invalid: v$.fechaCreacion.$invalid }"
                required
                :value="convertDateTimeFromServer(v$.fechaCreacion.$model)"
                @change="updateInstantField('fechaCreacion', $event)"
              />
            </div>
            <div v-if="v$.fechaCreacion.$anyDirty && v$.fechaCreacion.$invalid">
              <small class="form-text text-danger" v-for="error of v$.fechaCreacion.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('ventanillaUnicaApp.pqrs.fechaLimiteRespuesta')"
              for="pqrs-fechaLimiteRespuesta"
            ></label>
            <div class="d-flex">
              <input
                id="pqrs-fechaLimiteRespuesta"
                data-cy="fechaLimiteRespuesta"
                type="datetime-local"
                class="form-control"
                name="fechaLimiteRespuesta"
                :class="{ valid: !v$.fechaLimiteRespuesta.$invalid, invalid: v$.fechaLimiteRespuesta.$invalid }"
                :value="convertDateTimeFromServer(v$.fechaLimiteRespuesta.$model)"
                @change="updateInstantField('fechaLimiteRespuesta', $event)"
              />
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('ventanillaUnicaApp.pqrs.estado')" for="pqrs-estado"></label>
            <input
              type="text"
              class="form-control"
              name="estado"
              id="pqrs-estado"
              data-cy="estado"
              :class="{ valid: !v$.estado.$invalid, invalid: v$.estado.$invalid }"
              v-model="v$.estado.$model"
              required
            />
            <div v-if="v$.estado.$anyDirty && v$.estado.$invalid">
              <small class="form-text text-danger" v-for="error of v$.estado.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('ventanillaUnicaApp.pqrs.oficinaResponder')" for="pqrs-oficinaResponder"></label>
            <select
              class="form-control"
              id="pqrs-oficinaResponder"
              data-cy="oficinaResponder"
              name="oficinaResponder"
              v-model="pqrs.oficinaResponder"
            >
              <option
                :value="pqrs.oficinaResponder && oficinaOption.id === pqrs.oficinaResponder.id ? pqrs.oficinaResponder : oficinaOption"
                v-for="oficinaOption in oficinas"
                :key="oficinaOption.id"
              >
                {{ oficinaOption.nombre }}
              </option>
            </select>
          </div>
        </div>
        <div>
          <!--
          <div>
            <h2>Subir Archivo para PQRS</h2>

            <form @submit.prevent="uploadFile">
              <div>
                <label for="archivo">Adjuntar Archivo:</label>
                <input id="archivo" type="file" @change="onFileChange" required />
              </div>

              <p v-if="archivo">{{ archivo.name }} ({{ (archivo.size / 1024).toFixed(2) }} KB)</p>

              <button type="submit" :disabled="isUploading">Subir Archivo</button>
            </form>

            <button @click="previousState">Cancelar</button>
          </div>
-->
          <!-- Sección de archivos adjuntos -->
          <div>
            <h3>Archivos Adjuntos</h3>
            <ul v-if="pqrs.archivosAdjuntos && pqrs.archivosAdjuntos.length > 0">
              <li v-for="archivo in pqrs.archivosAdjuntos" :key="archivo.id">
                <a :href="archivo.urlArchivo" target="_blank">{{ archivo.nombre }}</a>
                ({{ archivo.tipo }} - Subido el {{ formatDate(archivo.fechaSubida) }})
              </li>
            </ul>
            <p v-else>No hay archivos adjuntos.</p>
          </div>

          <!-- Subir nuevos archivos -->
          <div>
            <h3>Subir nuevos archivos</h3>
            <input type="file" @change="onFileChange" multiple />
            <button type="button" @click="uploadFile" :disabled="isUploading">
              {{ isUploading ? 'Subiendo...' : 'Subir archivos' }}
            </button>
          </div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" @click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.cancel')"></span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="v$.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.save')"></span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./pqrs-update.component.ts"></script>
