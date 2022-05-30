package ztn.webclient.ztnwebclient.client;

import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import ztn.webclient.ztnwebclient.model.BeerDto;
import ztn.webclient.ztnwebclient.model.BeerPagedList;

import java.util.UUID;

public interface BeerClient {
    Mono<BeerDto> getBeerById(UUID id, Boolean showInventoryOnHand);

    Mono<BeerPagedList> listBeers(Integer pageNumber, Integer pageSize, String beerName, String beerStyle, Boolean showInventoryOnHand);

    Mono<ResponseEntity<Void>> createBeer(BeerDto beerDto);

    Mono<ResponseEntity> updateBeer(BeerDto beerDto);

    Mono<ResponseEntity> deleteBeer(UUID id);

    Mono<BeerDto> getBeerByUpc(String upc);
}
