// Errors
export const PROBLEM_BASE_URL = 'https://www.jhipster.tech/problem';
export const EMAIL_ALREADY_USED_TYPE = `${PROBLEM_BASE_URL}/email-already-used`;
export const LOGIN_ALREADY_USED_TYPE = `${PROBLEM_BASE_URL}/login-already-used`;

export enum StatesPqrs {
  Pending = 'PENDIENTE',
  InProcess = 'EN PROCESO',
  Resolved = 'RESUELTA',
  Closed = 'CERRADA',
  Rejected = 'RECHAZADA',
}
