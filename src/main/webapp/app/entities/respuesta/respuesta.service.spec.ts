/* tslint:disable max-line-length */
import axios from 'axios';
import sinon from 'sinon';
import dayjs from 'dayjs';

import RespuestaService from './respuesta.service';
import { DATE_TIME_FORMAT } from '@/shared/composables/date-format';
import { Respuesta } from '@/shared/model/respuesta.model';

const error = {
  response: {
    status: null,
    data: {
      type: null,
    },
  },
};

const axiosStub = {
  get: sinon.stub(axios, 'get'),
  post: sinon.stub(axios, 'post'),
  put: sinon.stub(axios, 'put'),
  patch: sinon.stub(axios, 'patch'),
  delete: sinon.stub(axios, 'delete'),
};

describe('Service Tests', () => {
  describe('Respuesta Service', () => {
    let service: RespuestaService;
    let elemDefault;
    let currentDate: Date;

    beforeEach(() => {
      service = new RespuestaService();
      currentDate = new Date();
      elemDefault = new Respuesta('ABC', 'AAAAAAA', currentDate, 'AAAAAAA');
    });

    describe('Service methods', () => {
      it('should find an element', async () => {
        const returnedFromService = { fechaRespuesta: dayjs(currentDate).format(DATE_TIME_FORMAT), ...elemDefault };
        axiosStub.get.resolves({ data: returnedFromService });

        return service.find('ABC').then(res => {
          expect(res).toMatchObject(elemDefault);
        });
      });

      it('should not find an element', async () => {
        axiosStub.get.rejects(error);
        return service
          .find('ABC')
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should create a Respuesta', async () => {
        const returnedFromService = { id: 'ABC', fechaRespuesta: dayjs(currentDate).format(DATE_TIME_FORMAT), ...elemDefault };
        const expected = { fechaRespuesta: currentDate, ...returnedFromService };

        axiosStub.post.resolves({ data: returnedFromService });
        return service.create({}).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not create a Respuesta', async () => {
        axiosStub.post.rejects(error);

        return service
          .create({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should update a Respuesta', async () => {
        const returnedFromService = {
          contenido: 'BBBBBB',
          fechaRespuesta: dayjs(currentDate).format(DATE_TIME_FORMAT),
          estado: 'BBBBBB',
          ...elemDefault,
        };

        const expected = { fechaRespuesta: currentDate, ...returnedFromService };
        axiosStub.put.resolves({ data: returnedFromService });

        return service.update(expected).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not update a Respuesta', async () => {
        axiosStub.put.rejects(error);

        return service
          .update({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should partial update a Respuesta', async () => {
        const patchObject = { fechaRespuesta: dayjs(currentDate).format(DATE_TIME_FORMAT), ...new Respuesta() };
        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = { fechaRespuesta: currentDate, ...returnedFromService };
        axiosStub.patch.resolves({ data: returnedFromService });

        return service.partialUpdate(patchObject).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not partial update a Respuesta', async () => {
        axiosStub.patch.rejects(error);

        return service
          .partialUpdate({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should return a list of Respuesta', async () => {
        const returnedFromService = {
          contenido: 'BBBBBB',
          fechaRespuesta: dayjs(currentDate).format(DATE_TIME_FORMAT),
          estado: 'BBBBBB',
          ...elemDefault,
        };
        const expected = { fechaRespuesta: currentDate, ...returnedFromService };
        axiosStub.get.resolves([returnedFromService]);
        return service.retrieve({ sort: {}, page: 0, size: 10 }).then(res => {
          expect(res).toContainEqual(expected);
        });
      });

      it('should not return a list of Respuesta', async () => {
        axiosStub.get.rejects(error);

        return service
          .retrieve()
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should delete a Respuesta', async () => {
        axiosStub.delete.resolves({ ok: true });
        return service.delete('ABC').then(res => {
          expect(res.ok).toBeTruthy();
        });
      });

      it('should not delete a Respuesta', async () => {
        axiosStub.delete.rejects(error);

        return service
          .delete('ABC')
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });
    });
  });
});