<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="pqrs">
        <h2 class="jh-entity-heading" data-cy="pqrsDetailsHeading">
          <span v-text="t$('ventanillaUnicaApp.pqrs.detail.title')"></span> {{ pqrs.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('ventanillaUnicaApp.pqrs.titulo')"></span>
          </dt>
          <dd>
            <span>{{ pqrs.titulo }}</span>
          </dd>
          <dt>
            <span v-text="t$('ventanillaUnicaApp.pqrs.descripcion')"></span>
          </dt>
          <dd>
            <span>{{ pqrs.descripcion }}</span>
          </dd>
          <dt>
            <span v-text="t$('ventanillaUnicaApp.pqrs.fechaCreacion')"></span>
          </dt>
          <dd>
            <span v-if="pqrs.fechaCreacion">{{ formatDateLong(pqrs.fechaCreacion) }}</span>
          </dd>
          <dt>
            <span v-text="t$('ventanillaUnicaApp.pqrs.fechaLimiteRespuesta')"></span>
          </dt>
          <dd>
            <span v-if="pqrs.fechaLimiteRespuesta">{{ formatDateLong(pqrs.fechaLimiteRespuesta) }}</span>
          </dd>
          <dt>
            <span v-text="t$('ventanillaUnicaApp.pqrs.estado')"></span>
          </dt>
          <dd>
            <span>{{ pqrs.estado }}</span>
          </dd>
          <dt>
            <span v-text="t$('ventanillaUnicaApp.pqrs.oficinaResponder')"></span>
          </dt>
          <dd>
            <div v-if="pqrs.oficinaResponder">
              <router-link :to="{ name: 'OficinaView', params: { oficinaId: pqrs.oficinaResponder.id } }">{{
                pqrs.oficinaResponder.id
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>

        <router-link v-if="pqrs.id" :to="{ name: 'PqrsEdit', params: { pqrsId: pqrs.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./pqrs-details.component.ts"></script>
