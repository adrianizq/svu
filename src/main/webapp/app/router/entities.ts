import { Authority } from '@/shared/security/authority';
/* tslint:disable */
// prettier-ignore
const Entities = () => import('@/entities/entities.vue');

const Oficina = () => import('@/entities/oficina/oficina.vue');
const OficinaUpdate = () => import('@/entities/oficina/oficina-update.vue');
const OficinaDetails = () => import('@/entities/oficina/oficina-details.vue');

const Pqrs = () => import('@/entities/pqrs/pqrs.vue');
const PqrsUpdate = () => import('@/entities/pqrs/pqrs-update.vue');
const PqrsDetails = () => import('@/entities/pqrs/pqrs-details.vue');

const Respuesta = () => import('@/entities/respuesta/respuesta.vue');
const RespuestaUpdate = () => import('@/entities/respuesta/respuesta-update.vue');
const RespuestaDetails = () => import('@/entities/respuesta/respuesta-details.vue');

const ArchivoAdjunto = () => import('@/entities/archivo-adjunto/archivo-adjunto.vue');
const ArchivoAdjuntoUpdate = () => import('@/entities/archivo-adjunto/archivo-adjunto-update.vue');
const ArchivoAdjuntoDetails = () => import('@/entities/archivo-adjunto/archivo-adjunto-details.vue');

const Notificacion = () => import('@/entities/notificacion/notificacion.vue');
const NotificacionUpdate = () => import('@/entities/notificacion/notificacion-update.vue');
const NotificacionDetails = () => import('@/entities/notificacion/notificacion-details.vue');

const InformePqrs = () => import('@/entities/informe-pqrs/informe-pqrs.vue');
const InformePqrsUpdate = () => import('@/entities/informe-pqrs/informe-pqrs-update.vue');
const InformePqrsDetails = () => import('@/entities/informe-pqrs/informe-pqrs-details.vue');

// jhipster-needle-add-entity-to-router-import - JHipster will import entities to the router here

export default {
  path: '/',
  component: Entities,
  children: [
    {
      path: 'oficina',
      name: 'Oficina',
      component: Oficina,
      meta: { authorities: [Authority.ADMIN] },
    },
    {
      path: 'oficina/new',
      name: 'OficinaCreate',
      component: OficinaUpdate,
      meta: { authorities: [Authority.ADMIN] },
    },
    {
      path: 'oficina/:oficinaId/edit',
      name: 'OficinaEdit',
      component: OficinaUpdate,
      meta: { authorities: [Authority.ADMIN] },
    },
    {
      path: 'oficina/:oficinaId/view',
      name: 'OficinaView',
      component: OficinaDetails,
      meta: { authorities: [Authority.ADMIN] },
    },
    {
      path: 'pqrs',
      name: 'Pqrs',
      component: Pqrs,
      meta: { authorities: [Authority.FUNCTIONARY, Authority.ADMIN] },
    },
    {
      path: 'pqrs/new',
      name: 'PqrsCreate',
      component: PqrsUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'pqrs/:pqrsId/edit',
      name: 'PqrsEdit',
      component: PqrsUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'pqrs/:pqrsId/view',
      name: 'PqrsView',
      component: PqrsDetails,
      meta: { authorities: [Authority.FUNCTIONARY, Authority.ADMIN] },
    },
    {
      path: 'respuesta',
      name: 'Respuesta',
      component: Respuesta,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'respuesta/new',
      name: 'RespuestaCreate',
      component: RespuestaUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'respuesta/:respuestaId/edit',
      name: 'RespuestaEdit',
      component: RespuestaUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'respuesta/:respuestaId/view',
      name: 'RespuestaView',
      component: RespuestaDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'archivo-adjunto',
      name: 'ArchivoAdjunto',
      component: ArchivoAdjunto,
      meta: { authorities: [Authority.ADMIN] },
    },
    {
      path: 'archivo-adjunto/new',
      name: 'ArchivoAdjuntoCreate',
      component: ArchivoAdjuntoUpdate,
      meta: { authorities: [Authority.ADMIN] },
    },
    {
      path: 'archivo-adjunto/:archivoAdjuntoId/edit',
      name: 'ArchivoAdjuntoEdit',
      component: ArchivoAdjuntoUpdate,
      meta: { authorities: [Authority.ADMIN] },
    },
    {
      path: 'archivo-adjunto/:archivoAdjuntoId/view',
      name: 'ArchivoAdjuntoView',
      component: ArchivoAdjuntoDetails,
      meta: { authorities: [Authority.ADMIN] },
    },
    {
      path: 'notificacion',
      name: 'Notificacion',
      component: Notificacion,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'notificacion/new',
      name: 'NotificacionCreate',
      component: NotificacionUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'notificacion/:notificacionId/edit',
      name: 'NotificacionEdit',
      component: NotificacionUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'notificacion/:notificacionId/view',
      name: 'NotificacionView',
      component: NotificacionDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'informe-pqrs',
      name: 'InformePqrs',
      component: InformePqrs,
      meta: { authorities: [Authority.ADMIN] },
    },
    {
      path: 'informe-pqrs/new',
      name: 'InformePqrsCreate',
      component: InformePqrsUpdate,
      meta: { authorities: [Authority.ADMIN] },
    },
    {
      path: 'informe-pqrs/:informePqrsId/edit',
      name: 'InformePqrsEdit',
      component: InformePqrsUpdate,
      meta: { authorities: [Authority.ADMIN] },
    },
    {
      path: 'informe-pqrs/:informePqrsId/view',
      name: 'InformePqrsView',
      component: InformePqrsDetails,
      meta: { authorities: [Authority.ADMIN] },
    },
    // jhipster-needle-add-entity-to-router - JHipster will add entities to the router here
  ],
};
