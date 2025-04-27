package co.edu.itp.svu.service;

import co.edu.itp.svu.domain.Oficina;
import co.edu.itp.svu.domain.Pqrs;
import co.edu.itp.svu.domain.User;
import co.edu.itp.svu.repository.OficinaRepository;
import co.edu.itp.svu.repository.PqrsRepository;
import co.edu.itp.svu.service.dto.OficinaDTO;
import co.edu.itp.svu.service.mapper.OficinaMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link co.edu.itp.svu.domain.Oficina}.
 */
@Service
public class OficinaService {

    private static final Logger LOG = LoggerFactory.getLogger(OficinaService.class);

    private UserService userService;

    private final OficinaRepository oficinaRepository;

    private final PqrsRepository pqrsRepository;

    private final OficinaMapper oficinaMapper;

    public OficinaService(
        OficinaRepository oficinaRepository,
        OficinaMapper oficinaMapper,
        UserService userService,
        PqrsRepository pqrsRepository
    ) {
        this.oficinaRepository = oficinaRepository;
        this.oficinaMapper = oficinaMapper;
        this.userService = userService;
        this.pqrsRepository = pqrsRepository;
    }

    /**
     * Save a oficina.
     *
     * @param oficinaDTO the entity to save.
     * @return the persisted entity.
     */
    public OficinaDTO save(OficinaDTO oficinaDTO) {
        LOG.debug("Request to save Oficina : {}", oficinaDTO);
        Oficina oficina = oficinaMapper.toEntity(oficinaDTO);
        oficina = oficinaRepository.save(oficina);
        return oficinaMapper.toDto(oficina);
    }

    /**
     * Update a oficina.
     *
     * @param oficinaDTO the entity to save.
     * @return the persisted entity.
     */
    public OficinaDTO update(OficinaDTO oficinaDTO) {
        LOG.debug("Request to update Oficina : {}", oficinaDTO);
        Oficina oficina = oficinaMapper.toEntity(oficinaDTO);
        oficina = oficinaRepository.save(oficina);
        return oficinaMapper.toDto(oficina);
    }

    /**
     * Partially update a oficina.
     *
     * @param oficinaDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<OficinaDTO> partialUpdate(OficinaDTO oficinaDTO) {
        LOG.debug("Request to partially update Oficina : {}", oficinaDTO);

        return oficinaRepository
            .findById(oficinaDTO.getId())
            .map(existingOficina -> {
                oficinaMapper.partialUpdate(existingOficina, oficinaDTO);

                return existingOficina;
            })
            .map(oficinaRepository::save)
            .map(oficinaMapper::toDto);
    }

    /**
     * Get all the oficinas.
     *
     * @return the list of entities.
     */
    public List<OficinaDTO> findAll() {
        LOG.debug("Request to get all Oficinas");
        return oficinaRepository.findAll().stream().map(oficinaMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one oficina by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<OficinaDTO> findOne(String id) {
        LOG.debug("Request to get Oficina : {}", id);
        return oficinaRepository.findById(id).map(oficinaMapper::toDto);
    }

    /**
     * Delete the oficina by id.
     *
     * @param id the id of the entity.
     */
    public void delete(String id) {
        LOG.debug("Request to delete Oficina : {}", id);
        oficinaRepository.deleteById(id);
    }

    /////////////////modificaciones////////////////////////////////////////7

    public OficinaDTO createOficina(OficinaDTO oficinaDTO) {
        LOG.debug("Request to save Oficina : {}", oficinaDTO);

        // Validación inicial del DTO
        if (oficinaDTO == null) {
            LOG.error("OficinaDTO no puede ser null");
            throw new IllegalArgumentException("OficinaDTO no puede ser null");
        }

        // Validar campos obligatorios
        if (oficinaDTO.getNombre() == null || oficinaDTO.getNombre().isEmpty()) {
            LOG.error("El nombre de la oficina es requerido");
            throw new IllegalArgumentException("El nombre de la oficina es requerido");
        }

        if (oficinaDTO.getResponsableDTO() == null || oficinaDTO.getResponsableDTO().getLogin() == null) {
            LOG.error("El usuario responsable es requerido");
            throw new IllegalArgumentException("El usuario responsable es requerido");
        }

        String usernameResponsable = oficinaDTO.getResponsableDTO().getLogin();
        Optional<User> responsable = userService.getUserWithAuthoritiesByLogin(usernameResponsable);

        if (!responsable.isPresent()) {
            LOG.warn("El usuario responsable no existe: {}", usernameResponsable);
            throw new IllegalArgumentException("El usuario responsable no existe");
        }

        // Mapeo del DTO a la entidad
        Oficina oficina;
        try {
            oficina = oficinaMapper.toEntity(oficinaDTO);
            if (oficina == null) {
                LOG.error("Error al mapear OficinaDTO a Oficina");
                throw new IllegalArgumentException("Error al mapear OficinaDTO a Oficina");
            }

            oficina.setResponsable(responsable.orElseThrow()); // Asociar el User como responsable
            oficina = oficinaRepository.save(oficina);
        } catch (Exception e) {
            LOG.error("Error al guardar la oficina: {}", e.getMessage());
            throw new RuntimeException("Error al guardar la oficina", e);
        }

        LOG.info("Oficina creada con éxito: {}", oficina);
        return oficinaMapper.toDto(oficina);
    }

    // Actualizar una oficina existente un usuario responsable
    public OficinaDTO updateOficina(OficinaDTO oficinaDTO) {
        LOG.debug("Request to update Oficina : {}", oficinaDTO);
        Oficina oficina = oficinaMapper.toEntity(oficinaDTO);
        Optional<Oficina> oficinaOptional = oficinaRepository.findById(oficinaDTO.getId());
        if (oficinaOptional.isPresent()) {
            String usernameResponsable = oficinaDTO.getResponsableDTO().getLogin();
            oficina = oficinaOptional.orElseThrow();
            oficina.setNombre(oficinaDTO.getNombre());
            oficina.setOficinaSuperior(oficinaDTO.getOficinaSuperior());
            oficina.setDescripcion(oficinaDTO.getDescripcion());
            oficina.setNivel(oficinaDTO.getNivel());
            Optional<User> responsable = userService.getUserWithAuthoritiesByLogin(usernameResponsable);
            if (responsable.isPresent()) {
                oficina.setResponsable(responsable.orElseThrow());
                oficina = oficinaRepository.save(oficina);
                return oficinaMapper.toDto(oficina);
            } else {
                throw new IllegalArgumentException("El usuario responsable no existe");
            }
        } else {
            throw new IllegalArgumentException("La oficina no existe");
        }
    }

    //Obtener una Oficina con cada
    public Optional<OficinaDTO> getOficina(String id) {
        Optional<Oficina> oficinaOpt = oficinaRepository.findById(id);

        if (oficinaOpt.isPresent()) {
            Oficina oficina = oficinaOpt.orElseThrow(); // Obtener la oficina
            List<Pqrs> allPqrs = pqrsRepository.findByOficinaResponder_Id(id); // Obtener todas las PQRS

            // Filtrar y agregar PQRS que pertenecen a esta oficina
            for (Pqrs pqrs : allPqrs) {
                if (pqrs.getOficinaResponder().getId().equals(oficina.getId())) {
                    oficina.addPqrs(pqrs);
                }
            }
        }

        return oficinaOpt.map(oficinaMapper::toDto);
    }

    ////////////////777
    public Optional<OficinaDTO> getOficinaUser(String id) {
        Optional<Oficina> oficinaOpt = Optional.ofNullable(oficinaRepository.findByResponsable_Id(id));

        if (oficinaOpt.isPresent()) {
            Oficina oficina = oficinaOpt.orElseThrow(); // Obtener la oficina
            List<Pqrs> allPqrs = pqrsRepository.findByOficinaResponder_Id(oficinaOpt.orElseThrow().getId()); // Obtener todas las PQRS

            // Filtrar y agregar PQRS que pertenecen a esta oficina
            for (Pqrs pqrs : allPqrs) {
                if (pqrs.getOficinaResponder().getId().equals(oficina.getId())) {
                    oficina.addPqrs(pqrs);
                }
            }
        }

        return oficinaOpt.map(oficinaMapper::toDto);
    }

    //oficina user con login
    public Optional<OficinaDTO> getOficinaUserLogin(String login) {
        String loginUser;
        Optional<User> user = userService.getUserByLogin(login);
        if (user.orElseThrow() != null) {
            Optional<Oficina> oficinaOpt = Optional.ofNullable(oficinaRepository.findByResponsable_Id(user.orElseThrow().getId()));

            if (oficinaOpt.isPresent()) {
                Oficina oficina = oficinaOpt.orElseThrow(); // Obtener la oficina
                List<Pqrs> allPqrs = pqrsRepository.findByOficinaResponder_Id(oficinaOpt.orElseThrow().getId()); // Obtener todas las PQRS

                // Filtrar y agregar PQRS que pertenecen a esta oficina
                for (Pqrs pqrs : allPqrs) {
                    if (pqrs.getOficinaResponder().getId().equals(oficina.getId())) {
                        oficina.addPqrs(pqrs);
                    }
                }
                return oficinaOpt.map(oficinaMapper::toDto);
            }
        }
        Optional<Oficina> of = Optional.of(new Oficina());
        of.orElseThrow().setId("");
        return of.map(oficinaMapper::toDto);
    }
}
