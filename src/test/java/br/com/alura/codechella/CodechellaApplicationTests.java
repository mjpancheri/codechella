package br.com.alura.codechella;

import br.com.alura.codechella.model.TipoEvento;
import br.com.alura.codechella.model.dto.EventoDto;
import br.com.alura.codechella.service.EventoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CodechellaApplicationTests {

	@Autowired
	private WebTestClient webTestClient;
	@Autowired
	private EventoService service;

	@Test
	void cadastraNovoEvento() {
		EventoDto dto = new EventoDto(null, TipoEvento.SHOW, "Kiss",
				LocalDate.parse("2025-01-01"), "Show da melhor banda que existe");

		webTestClient.post().uri("/eventos").bodyValue(dto)
				.exchange()
				.expectStatus().isCreated()
				.expectBody(EventoDto.class)
				.value(response -> {
					assertNotNull(response.id());
					assertEquals(dto.tipo(), response.tipo());
					assertEquals(dto.nome(), response.nome());
					assertEquals(dto.data(), response.data());
					assertEquals(dto.descricao(), response.descricao());
				});
	}

	// código omitido

	@Test
	void buscarEvento() {
		EventoDto dto = new EventoDto(1L, TipoEvento.SHOW, "Taylor Swift",
				LocalDate.parse("2024-02-15"), "Um evento imperdível para todos os amantes da música pop.");

		webTestClient.get().uri("/eventos")
				.exchange()
				.expectStatus().is2xxSuccessful()
				.expectBodyList(EventoDto.class)
				.value(response -> {
					EventoDto eventoResponse = response.get(0);
					assertEquals(dto.id(), eventoResponse.id());
					assertEquals(dto.tipo(), eventoResponse.tipo());
					assertEquals(dto.nome(), eventoResponse.nome());
					assertEquals(dto.data(), eventoResponse.data());
					assertEquals(dto.descricao(), eventoResponse.descricao());
				});
	}

	@Test
	void alteraEvento() {
		EventoDto dtoAtualizado = new EventoDto(5L, TipoEvento.CONCERTO, "Metallica",
				LocalDate.parse("2025-12-01"), "Concerto da banda Metallica");

		webTestClient.put().uri("/eventos/{id}", 5L).bodyValue(dtoAtualizado)
				.exchange()
				.expectStatus().isOk()
				.expectBody(EventoDto.class)
				.value(response -> {
					assertEquals(dtoAtualizado.id(), response.id());
					assertEquals(dtoAtualizado.tipo(), response.tipo());
					assertEquals(dtoAtualizado.nome(), response.nome());
					assertEquals(dtoAtualizado.data(), response.data());
					assertEquals(dtoAtualizado.descricao(), response.descricao());
				});
	}

	@Test
	void excluiEvento() {
		webTestClient.delete().uri("/eventos/{id}", 10L)
				.exchange()
				.expectStatus().isNoContent();

		webTestClient.get().uri("/eventos/{id}", 10L)
				.exchange()
				.expectStatus().isNotFound();
	}

	//@Test
	void testServiceMethod() {
		Flux<EventoDto> flux = service.obterTodos();

		EventoDto dto1 = new EventoDto(1L, TipoEvento.SHOW, "Banda 1", LocalDate.parse("2025-01-01"), "Descricao");
		EventoDto dto2 = new EventoDto(1L, TipoEvento.SHOW, "Banda 1", LocalDate.parse("2025-01-01"), "Descricao");

		StepVerifier.create(flux)
				.expectNext(dto1, dto2)
				.expectComplete()
				.verify();
	}

	//@Test
	void testWithTestPublisher() {
		TestPublisher<String> publisher = TestPublisher.create();

		Flux<String> flux = publisher.flux();

		StepVerifier.create(flux)
				.then(() -> publisher.emit("data1", "data2"))
				.expectNext("data1", "data2")
				.expectComplete()
				.verify();
	}

}
