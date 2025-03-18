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
          <!-- Subida de Archivos 
 <b-form-group label="Archivos Adjuntos">
        <b-form-file multiple @change="onFileChange"></b-form-file>
        <ul v-if="archivosAdjuntos.length">
          <li v-for="(file, index) in archivosAdjuntos" :key="index">
            {{ file.name }}
          </li>
        </ul>
      </b-form-group> -->
          <div>
            <h2>Subir Archivo para PQRS</h2>

            <form @submit.prevent="uploadFile">
              <!-- ✅ Campo para subir archivo -->
              <div>
                <label for="archivo">Adjuntar Archivo:</label>
                <input id="archivo" type="file" @change="onFileChange" required />
              </div>

              <!-- ✅ Vista previa del archivo seleccionado -->
              <p v-if="archivo">{{ archivo.name }} ({{ (archivo.size / 1024).toFixed(2) }} KB)</p>

              <!-- ✅ Botón para subir -->
              <button type="submit" :disabled="isUploading">Subir Archivo</button>
            </form>

            <button @click="previousState">Cancelar</button>
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
