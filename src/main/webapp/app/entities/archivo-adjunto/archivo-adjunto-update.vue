<template>
  <div class="form-group">
    <label for="archivo-adjunto-urlArchivo">
      {{ t$('ventanillaUnicaApp.archivoAdjunto.urlArchivo') }}
    </label>
    <!-- Input para cargar un nuevo archivo -->
    <input type="file" id="archivo" @change="handleFileChange" class="form-control" />

    <!-- Botón para subir archivo -->
    <button class="btn btn-primary mt-2" @click="uploadFile" :disabled="!file">Subir Archivo</button>

    <!-- Mostrar mensaje de éxito o error -->
    <div v-if="successMessage" class="alert alert-success mt-2">
      {{ successMessage }}
    </div>
    <div v-if="errorMessage" class="alert alert-danger mt-2">
      {{ errorMessage }}
    </div>
  </div>
</template>
<script lang="ts" src="./archivo-adjunto-update.component.ts"></script>
