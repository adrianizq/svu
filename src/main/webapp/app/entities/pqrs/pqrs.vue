<template>
  <div>
    <h2 id="page-heading" data-cy="PqrsHeading">
      <span v-text="t$('ventanillaUnicaApp.pqrs.home.title')" id="pqrs-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('ventanillaUnicaApp.pqrs.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'PqrsCreate' }" custom v-slot="{ navigate }">
          <!--<button @click="navigate" id="jh-create-entity" data-cy="entityCreateButton" class="btn btn-primary jh-create-entity create-pqrs"> -->
          <button
            v-if="esAdmin"
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-pqrs"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('ventanillaUnicaApp.pqrs.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && pqrs && pqrs.length === 0">
      <span v-text="t$('ventanillaUnicaApp.pqrs.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="pqrs && pqrs.length > 0">
      <table class="table table-striped" aria-describedby="pqrs">
        <thead>
          <tr>
            <th scope="row" @click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('titulo')">
              <span v-text="t$('ventanillaUnicaApp.pqrs.titulo')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'titulo'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('descripcion')">
              <span v-text="t$('ventanillaUnicaApp.pqrs.descripcion')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'descripcion'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('fechaCreacion')">
              <span v-text="t$('ventanillaUnicaApp.pqrs.fechaCreacion')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'fechaCreacion'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('fechaLimiteRespuesta')">
              <span v-text="t$('ventanillaUnicaApp.pqrs.fechaLimiteRespuesta')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'fechaLimiteRespuesta'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('estado')">
              <span v-text="t$('ventanillaUnicaApp.pqrs.estado')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'estado'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('oficinaResponder.id')">
              <span v-text="t$('ventanillaUnicaApp.pqrs.oficinaResponder.')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'oficinaResponder.id'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="pqrs in pqrs" :key="pqrs.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'PqrsView', params: { pqrsId: pqrs.id } }">{{ pqrs.id }}</router-link>
            </td>
            <td>{{ pqrs.titulo }}</td>
            <td>{{ pqrs.descripcion }}</td>
            <td>{{ formatDateShort(pqrs.fechaCreacion) || '' }}</td>
            <td>{{ formatDateShort(pqrs.fechaLimiteRespuesta) || '' }}</td>
            <td>{{ pqrs.estado }}</td>
            <td>
              <div v-if="pqrs.oficinaResponder">
                <router-link :to="{ name: 'OficinaView', params: { oficinaId: pqrs.oficinaResponder.id } }">{{
                  pqrs.oficinaResponder.nombre
                }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'PqrsView', params: { pqrsId: pqrs.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'PqrsEdit', params: { pqrsId: pqrs.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(pqrs)"
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
        <span id="ventanillaUnicaApp.pqrs.delete.question" data-cy="pqrsDeleteDialogHeading" v-text="t$('entity.delete.title')"></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-pqrs-heading" v-text="t$('ventanillaUnicaApp.pqrs.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" @click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-pqrs"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            @click="removePqrs()"
          ></button>
        </div>
      </template>
    </b-modal>
    <div v-show="pqrs && pqrs.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./pqrs.component.ts"></script>
