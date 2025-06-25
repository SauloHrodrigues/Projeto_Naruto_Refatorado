package com.naruto.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.naruto.dto.jutsu.JutsuRequestDto;
import com.naruto.dto.jutsu.JutsuResponseDto;
import com.naruto.dto.personagem.NovoPersonagemDTO;
import com.naruto.dto.personagem.PersonagemResponseDto;
import com.naruto.exceptions.personagem.PersonagemJaCadastradoException;
import com.naruto.fixture.JutsuFixture;
import com.naruto.fixture.PersonagemFixture;
import com.naruto.service.implementacao.PersonagemServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;

@WebMvcTest(PersonagemController.class)
class PersonagemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;   // já configurado pelo Spring Boot Test

    @MockBean

    private PersonagemServiceImp service;

    JutsuRequestDto jutsuRequestDto;
    JutsuResponseDto jutsuResponseDto;
    NovoPersonagemDTO personagemDTO;


    @BeforeEach
    void setUp() {
        jutsuRequestDto = JutsuFixture.requestDto("soco",5,10);
        jutsuResponseDto = JutsuFixture.response(2L,jutsuRequestDto);
        personagemDTO = PersonagemFixture.novoDto("Naruto","NINJA_DE_NINJUTSU",20,5, jutsuRequestDto );

    }

    @Test
    void cadastrar_deveRetornar201_quandoSucesso() throws Exception {
        // ---------- arrange ----------

        PersonagemResponseDto resposta = PersonagemFixture.responseDto(1L, personagemDTO,jutsuResponseDto);

        when(service.novoPersonagem(any())).thenReturn(resposta);

        // ---------- act + assert ----------
        mockMvc.perform(post("/personagem").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personagemDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id",equalTo(resposta.id().intValue())))
                .andExpect(jsonPath("$.nome", is(resposta.nome())))
                .andExpect(jsonPath("$.chakra", is(resposta.chakra())))
                .andExpect(jsonPath("$.jutsus.soco").exists())
                .andExpect(jsonPath("$.jutsus.soco.id", equalTo(jutsuResponseDto.id().intValue())));
    }

    @Test
    void cadastrar_deveRetornar409_PersonagemJaCadastrado() throws Exception {

        when(service.novoPersonagem(personagemDTO)).thenThrow(new PersonagemJaCadastradoException(
                "O personagem Naruto já está cadastrado no sistema"));

        mockMvc.perform(post("/personagem")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personagemDTO)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.mensagem", is("O personagem "+personagemDTO.nome()+" já está cadastrado no sistema")));
   }

    @Test
    void listar() {
    }

    @Test
    void adicionarJutsu() {
    }

    @Test
    void apagar() {
    }
}