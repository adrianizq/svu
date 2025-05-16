// Errors
export const PROBLEM_BASE_URL = 'https://www.jhipster.tech/problem';
export const EMAIL_ALREADY_USED_TYPE = `${PROBLEM_BASE_URL}/email-already-used`;
export const LOGIN_ALREADY_USED_TYPE = `${PROBLEM_BASE_URL}/login-already-used`;

export const LISTA_ESTADOS_PQRS: ReadonlyArray<EstadoPqrsSimple> = Object.freeze([
  { valor: 'pendiente', etiqueta: 'Pendiente' },
  { valor: 'en_proceso', etiqueta: 'En Proceso' },
  { valor: 'resuelto', etiqueta: 'Resuelto' },
  { valor: 'closed', etiqueta: 'Cerrado' },
]);
