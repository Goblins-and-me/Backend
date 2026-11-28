package com.shop.SweetStory.catalog.filling.service;

import com.shop.SweetStory.catalog.filling.dto.*;
import com.shop.SweetStory.catalog.filling.entity.Filling;
import com.shop.SweetStory.catalog.filling.repository.FillingRepository;
import com.shop.SweetStory.catalog.generalDto.ActionMessageResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class FillingService {

        private final FillingRepository fillingRepository;

        public FillingService(FillingRepository repository){
            this.fillingRepository = repository;
        }

        public FillingListResponse getAllFillings(){
            List<FillingResponse> items = fillingRepository.findAll()
                    .stream()
                    .map(this::toFillingResponse)
                    .toList();

            return new FillingListResponse(items);
        }

        public FillingResponse getFilling(Long id){
            Filling filling = findFillingOrThrow(id);

            return toFillingResponse(filling);
        }

        @Transactional
        public CreateFillingResponse createFilling(CreateFillingRequest request){
            Filling filling = new Filling();
            filling.setDescription(request.description());
            filling.setName(request.name());
            filling.setPrice(request.price());

            Filling saved = fillingRepository.save(filling);

            return new CreateFillingResponse("success", toFillingResponse(saved));
        }

        @Transactional
        public ActionMessageResponse updateFilling(Long id, UpdateFillingRequest request){
            Filling filling = findFillingOrThrow(id);

            if(request.name() != null && !request.name().isBlank()){
                filling.setName(request.name().trim());
            }
            if(request.description() != null &&!request.description().isBlank()){
                filling.setDescription(request.description());
            }
            if(request.price() != null){
                filling.setPrice(request.price());
            }

            fillingRepository.save(filling);
            return new ActionMessageResponse("success", "decoration updated, brother");
        }

        @Transactional
        public ActionMessageResponse deleteFilling(Long id){
            Filling filling = findFillingOrThrow(id);
            fillingRepository.delete(filling);
            return new ActionMessageResponse("success", "deleted, bro");
        }

        private Filling findFillingOrThrow(Long id){
            return fillingRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Decoration not found"));
        }

        private FillingResponse toFillingResponse(Filling filling){
            return new FillingResponse(
                    filling.getId(),
                    filling.getName(),
                    filling.getDescription(),
                    filling.getPrice()
            );
        }

}
