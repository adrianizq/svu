<template>
  <div>
    <h2 id="page-heading" data-cy="OficinaHeading">
      <span v-text="t$('ventanillaUnicaApp.oficina.home.title')" id="oficina-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('ventanillaUnicaApp.oficina.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'OficinaUserCreate' }" custom v-slot="{ navigate }">
          <button
            v-if="esAdmin"
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-oficina"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('ventanillaUnicaApp.oficina.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && oficinas && oficinas.length === 0">
      <span v-text="t$('ventanillaUnicaApp.oficina.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="oficinas && oficinas.length > 0">
      <table class="table table-striped" aria-describedby="oficinas">
        <thead>
          <tr>
            <th scope="row"><span v-text="t$('global.field.id')"></span></th>
            <th scope="row"><span v-text="t$('ventanillaUnicaApp.oficina.nombre')"></span></th>
            <th scope="row"><span v-text="t$('ventanillaUnicaApp.oficina.descripcion')"></span></th>
            <th scope="row"><span v-text="t$('ventanillaUnicaApp.oficina.nivel')"></span></th>
            <th scope="row"><span v-text="t$('ventanillaUnicaApp.oficina.oficinaSuperior')"></span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="oficina in oficinas" :key="oficina.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'OficinaView', params: { oficinaId: oficina.id } }">{{ oficina.id }}</router-link>
            </td>
            <td>{{ oficina.nombre }}</td>
            <td>{{ oficina.descripcion }}</td>
            <td>{{ oficina.nivel }}</td>
            <td>{{ oficina.oficinaSuperior }}</td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'OficinaView', params: { oficinaId: oficina.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'OficinaEdit', params: { oficinaId: oficina.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(oficina)"
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
          id="ventanillaUnicaApp.oficina.delete.question"
          data-cy="oficinaDeleteDialogHeading"
          v-text="t$('entity.delete.title')"
        ></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-oficina-heading" v-text="t$('ventanillaUnicaApp.oficina.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" @click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-oficina"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            @click="removeOficina()"
          ></button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./oficina.component.ts"></script>
