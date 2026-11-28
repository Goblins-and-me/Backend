package com.shop.SweetStory.catalog.decoration.service;

import com.shop.SweetStory.catalog.decoration.dto.*;
import com.shop.SweetStory.catalog.decoration.entity.Decoration;
import com.shop.SweetStory.catalog.decoration.repository.DecorationRepository;
import com.shop.SweetStory.catalog.generalDto.ActionMessageResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
public class DecorationService {

    private final DecorationRepository decorationRepository;

    public DecorationService(DecorationRepository repository){
        this.decorationRepository = repository;
    }

    public DecorationListResponse getAllDecorations(){
        List<DecorationResponse> items = decorationRepository.findAll()
                .stream()
                .map(this::toDecorationResponse)
                .toList();

        return new DecorationListResponse(items);
    }

    public DecorationResponse getDecoration(Long id){
        Decoration decoration = findDecorationOrThrow(id);

        return toDecorationResponse(decoration);
    }

    @Transactional
    public CreateDecorationResponse createDecoration(CreateDecorationRequest request){
        Decoration decoration = new Decoration();
        decoration.setDescription(request.description());
        decoration.setName(request.name().trim());
        decoration.setPrice(request.price());

        Decoration saved = decorationRepository.save(decoration);

        return new CreateDecorationResponse("success", toDecorationResponse(saved));
    }

    @Transactional
    public ActionMessageResponse updateDecoration(Long id, UpdateDecorationRequest request){
        Decoration decoration = findDecorationOrThrow(id);

        if(request.name() != null && !request.name().isBlank()){
            decoration.setName(request.name().trim());
        }
        if(request.description() != null &&!request.description().isBlank()){
            decoration.setDescription(request.description());
        }
        if(request.price() != null){
            decoration.setPrice(request.price());
        }

        decorationRepository.save(decoration);
        return new ActionMessageResponse("success", "decoration updated, brother");
    }

    @Transactional
    public ActionMessageResponse deleteDecoration(Long id){
        Decoration decoration = findDecorationOrThrow(id);
        decorationRepository.delete(decoration);
        return new ActionMessageResponse("success", "deleted, bro");
    }

    private Decoration findDecorationOrThrow(Long id){
        return decorationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Decoration not found"));
    }

    private DecorationResponse toDecorationResponse(Decoration decoration){
        return new DecorationResponse(
                decoration.getId(),
                decoration.getName(),
                decoration.getDescription(),
                decoration.getPrice()
        );
    }



}
