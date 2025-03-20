<template>
  <div>
    <h2 id="page-heading" data-cy="ArchivoAdjuntoHeading">
      <span v-text="t$('ventanillaUnicaApp.archivoAdjunto.home.title')" id="archivo-adjunto-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('ventanillaUnicaApp.archivoAdjunto.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'ArchivoAdjuntoCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-archivo-adjunto"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('ventanillaUnicaApp.archivoAdjunto.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && archivoAdjuntos && archivoAdjuntos.length === 0">
      <span v-text="t$('ventanillaUnicaApp.archivoAdjunto.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="archivoAdjuntos && archivoAdjuntos.length > 0">
      <table class="table table-striped" aria-describedby="archivoAdjuntos">
        <thead>
          <tr>
            <th scope="row"><span v-text="t$('global.field.id')"></span></th>
            <th scope="row"><span v-text="t$('ventanillaUnicaApp.archivoAdjunto.nombre')"></span></th>
            <th scope="row"><span v-text="t$('ventanillaUnicaApp.archivoAdjunto.tipo')"></span></th>
            <th scope="row"><span v-text="t$('ventanillaUnicaApp.archivoAdjunto.urlArchivo')"></span></th>
            <th scope="row"><span v-text="t$('ventanillaUnicaApp.archivoAdjunto.fechaSubida')"></span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="archivoAdjunto in archivoAdjuntos" :key="archivoAdjunto.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'ArchivoAdjuntoView', params: { archivoAdjuntoId: archivoAdjunto.id } }">{{
                archivoAdjunto.id
              }}</router-link>
            </td>
            <td>{{ archivoAdjunto.nombre }}</td>
            <td>{{ archivoAdjunto.tipo }}</td>
            <td>{{ archivoAdjunto.urlArchivo }}</td>
            <td>{{ formatDateShort(archivoAdjunto.fechaSubida) || '' }}</td>

            <td class="text-right">
              <div class="btn-group">
                <router-link
                  :to="{ name: 'ArchivoAdjuntoView', params: { archivoAdjuntoId: archivoAdjunto.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link
                  :to="{ name: 'ArchivoAdjuntoEdit', params: { archivoAdjuntoId: archivoAdjunto.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(archivoAdjunto)"
                  variant="danger"
                  class="btn btn-sm"
                  data-cy="entityDeleteButton"
                  v-b-modal.removeEntity
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                  <span class="d-none d-md-inline" v-text="t$('entity.action.delete')"></span>
                </b-button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <b-modal ref="removeEntity" id="removeEntity">
      <template #modal-title>
        <span
          id="ventanillaUnicaApp.archivoAdjunto.delete.question"
          data-cy="archivoAdjuntoDeleteDialogHeading"
          v-text="t$('entity.delete.title')"
        ></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-archivoAdjunto-heading" v-text="t$('ventanillaUnicaApp.archivoAdjunto.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" @click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-archivoAdjunto"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            @click="removeArchivoAdjunto()"
          ></button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./archivo-adjunto.component.ts"></script>
