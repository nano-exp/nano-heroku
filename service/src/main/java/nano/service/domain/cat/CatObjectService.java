package nano.service.domain.cat;

import nano.service.domain.object.ObjectService;
import nano.service.nano.repository.KeyValueRepository;
import nano.service.nano.repository.NanoBlobRepository;
import org.springframework.stereotype.Service;

@Service
public class CatObjectService extends ObjectService {

    public CatObjectService(KeyValueRepository keyValueRepository, NanoBlobRepository nanoBlobRepository) {
        super(keyValueRepository, nanoBlobRepository, "cat");
    }
}
