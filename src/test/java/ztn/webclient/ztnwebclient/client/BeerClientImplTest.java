package ztn.webclient.ztnwebclient.client;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import ztn.webclient.ztnwebclient.config.WebClientConfig;
import ztn.webclient.ztnwebclient.model.BeerDto;
import ztn.webclient.ztnwebclient.model.BeerPagedList;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
@Slf4j
class BeerClientImplTest {

    BeerClient beerClient;

    @BeforeEach
    void setUp() {
        beerClient = new BeerClientImpl(new WebClientConfig().webClient());
    }

    @Test
    void listBeers() {
        Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(1, 5, null,
                null, null);
        BeerPagedList beerPagedList = beerPagedListMono.block();
        beerPagedList.stream().forEach(System.out::println);
    }

    @Test
    void getBeerById() throws InterruptedException {
        /*Mono<BeerDto> beerDtoMono = beerClient.getBeerById(UUID.fromString("c1c05355-5683-4566-a34a-74b7eae0163e"), null);
        BeerDto beerDto = beerDtoMono.block();
        System.out.println(beerDto);*/

        AtomicReference<String> beerName = new AtomicReference<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);

        beerClient.listBeers(null, null, null, null, null)
                .map(beerPagedList -> beerPagedList.getContent().get(0).getId())
                .map(beerId -> beerClient.getBeerById(beerId, false))
                .flatMap(beerDtoMono -> beerDtoMono)
                .subscribe(beerDto -> {
                    beerName.set(beerDto.getBeerName());
                    countDownLatch.countDown();
                });
        countDownLatch.await();
        System.out.println("Beer name is " + beerName.get());
    }

    @Test
    void createBeer() {
        BeerDto beerDto = BeerDto.builder()
                .beerName("Dogfishhead 90 Min IPA")
                .beerStyle("IPA")
                .upc("234848549559")
                .price(new BigDecimal("10.99"))
                .build();

        Mono<ResponseEntity<Void>> responseEntityMono = beerClient.createBeer(beerDto);

        ResponseEntity responseEntity = responseEntityMono.onErrorResume(throwable -> {
            if (throwable instanceof WebClientResponseException) {
                WebClientResponseException exception = (WebClientResponseException) throwable;
                return Mono.just(ResponseEntity.status(exception.getStatusCode()).build());
            } else {
                throw new RuntimeException(throwable);
            }
        }).block();
        System.out.println(responseEntity.getStatusCode());
    }

    @Test
    void testGetBeerById() {
    }
}