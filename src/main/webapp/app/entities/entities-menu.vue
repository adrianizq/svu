<template>
  <div>
    <b-dropdown-item v-if="isAdmin" to="/oficina">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.oficina')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/pqrs">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.pqrs')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/respuesta">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.respuesta')"></span>
    </b-dropdown-item>
    <b-dropdown-item v-if="isAdmin" to="/archivo-adjunto">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.archivoAdjunto')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/notificacion">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.notificacion')"></span>
    </b-dropdown-item>
    <b-dropdown-item v-if="isAdmin" to="/informe-pqrs">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.informePqrs')"></span>
    </b-dropdown-item>
    <!-- jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here -->
  </div>
</template>

<script lang="ts" src="./entities-menu.component.ts"></script>
