package com.dam.modules.dam.service;

import com.dam.modules.dam.model.Dam;
import com.dam.modules.dam.repository.DamStatusRepository;
import com.dam.modules.dam.repository.DamRepository;

import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;


@Service
public class DamService {

    private static final Logger LOG = Logger.getLogger(DamService.class.getName());
    private DamRepository damRepository;

    private DamStatusRepository damStatusRepository;
    private Java2DFrameConverter aa;

    @Autowired
    public DamService(DamRepository damRepository, DamStatusRepository damStatusRepository) {
        this.damRepository = damRepository;
        this.damStatusRepository = damStatusRepository;
    }

    public List<Dam> findAllDam(String sort,
                                int page,
                                int perPage) {
        Pageable sortedAndPagination =
                PageRequest.of(page, perPage, Sort.by(sort).ascending());

        return damRepository.getDams(sortedAndPagination);
    }

    @Transactional
    public Dam saveDam(Dam dam) throws IOException {

        return this.damRepository.save(dam);
    }

    @Transactional
    public Dam addDam(Dam dam, Path rootImages, String urlImages) throws IOException {

        Dam dam_saved = this.damRepository.save(dam);
        String imagePath = rootImages.toFile().getAbsolutePath();

        if (dam.getFileImage() != null && !dam.getFileImage().isEmpty()) {
            MultipartFile fileImage = dam.getFileImage();
            byte[] bytesImages = fileImage.getBytes();
            String fileNameImage = UUID.randomUUID() + "." + Objects.requireNonNull(fileImage.getContentType()).split("/")[1];
            Files.write(Paths.get(imagePath + File.separator + fileNameImage), bytesImages);
            dam.setPhoto(urlImages + "/" + fileNameImage);
        }
        return dam_saved;
    }

    @Transactional
    public Dam addDam(Dam dam ) throws IOException {

        Dam dam_saved = this.damRepository.save(dam);
        return dam_saved;
    }

    public void deleteDam(Long id) throws Exception {
        this.damRepository.deleteById(id);
    }

    public List<Dam> findAll(String sort,
                             int page,
                             int perPage,
                             Specification<Dam> spec) {
        Pageable sortedAndPagination =
                PageRequest.of(page, perPage, Sort.by(sort).ascending());
        return damRepository.findAll(spec, sortedAndPagination);
    }

    public List<Dam> findAll(String sort,
                             int page,
                             int perPage,
                             String userId) {
        Pageable sortedAndPagination =
                PageRequest.of(page, perPage, Sort.by(sort).ascending());
        if (userId.isEmpty()) {
            return damRepository.findAllDam(sortedAndPagination);
        } else {
            return damRepository.findDamByOwner(userId, sortedAndPagination);
        }

    }

    public Optional<Dam> findDam(Long id) {
        return this.damRepository.findById(id);
    }
}