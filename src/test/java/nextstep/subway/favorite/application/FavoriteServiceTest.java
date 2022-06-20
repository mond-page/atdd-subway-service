package nextstep.subway.favorite.application;

import static nextstep.subway.station.StationAcceptanceTest.지하철역_생성;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import nextstep.subway.exception.NotExistException;
import nextstep.subway.exception.NotLinkedPathException;
import nextstep.subway.favorite.domain.Favorite;
import nextstep.subway.favorite.domain.FavoriteRepository;
import nextstep.subway.favorite.dto.FavoriteRequest;
import nextstep.subway.favorite.dto.FavoriteResponse;
import nextstep.subway.member.application.MemberService;
import nextstep.subway.member.domain.Member;
import nextstep.subway.path.application.PathService;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {
    private final Member mond = new Member(1L, "mond@mond.com", "mond", 10);
    final FavoriteRequest favoriteRequest = new FavoriteRequest(1L, 2L);
    private final Station gangNam = 지하철역_생성(1L, "강남역");
    private final Station gyoDae = 지하철역_생성(2L, "교대역");

    private FavoriteService favoriteService;
    @Mock
    private StationService stationService;
    @Mock
    private MemberService memberService;
    @Mock
    private PathService pathService;
    @Mock
    private FavoriteRepository favoriteRepository;

    @BeforeEach
    void setUp() {
        favoriteService = new FavoriteService(stationService, memberService, pathService, favoriteRepository);
    }

    @Test
    @DisplayName("즐겨찾기를 생성한다")
    void saveFavorite() {
        // given
        when(stationService.findStationById(1L)).thenReturn(gangNam);
        when(stationService.findStationById(2L)).thenReturn(gyoDae);
        when(memberService.findMemberById(1L)).thenReturn(mond);
        when(favoriteRepository.save(any())).thenReturn(new Favorite(mond, gangNam, gyoDae));

        // when
        FavoriteResponse favoriteResponse = favoriteService.saveFavoriteOfMine(mond.getId(), favoriteRequest);

        // then
        verifySourceAndTarget(favoriteResponse.getSource(), favoriteResponse.getTarget(), gangNam, gyoDae);
    }

    @Test
    @DisplayName("즐겨찾기를 조회한다")
    void searchFavorites() {
        // given
        when(memberService.findMemberById(1L)).thenReturn(mond);
        when(favoriteRepository.findAllByMemberIdAndDeletedFalse(any())).thenReturn(
                Collections.singletonList(new Favorite(mond, gangNam, gyoDae)));

        // when
        List<FavoriteResponse> favoritesResponse = favoriteService.findFavoriteOfMine(1L);

        // then
        assertAll(
                () -> assertThat(favoritesResponse).hasSize(1),
                () -> verifySourceAndTarget(favoritesResponse.get(0).getSource(), favoritesResponse.get(0).getTarget(),
                        gangNam, gyoDae)
        );
    }

    @Test
    @DisplayName("즐겨찾기를 삭제한다")
    void deleteFavorite() {
        // given
        Optional<Favorite> optionalFavorite = Optional.of(new Favorite(mond, gangNam, gyoDae));
        when(favoriteRepository.findByIdAndMemberId(any(), any())).thenReturn(optionalFavorite);

        // when && then
        favoriteService.deleteFavoriteOfMine(1L, 1L);
    }

    @Test
    @DisplayName("생성되지 않는 역을 즐겨찾기 등록하려고 하면 실패한다")
    void saveFavoriteOfNotExistStation() {
        // given
        when(stationService.findStationById(1L)).thenReturn(gangNam);
        when(stationService.findStationById(2L)).thenThrow(NotExistException.class);

        // when && then
        assertThatIllegalArgumentException()
                .isThrownBy(() -> favoriteService.saveFavoriteOfMine(mond.getId(), favoriteRequest));
    }

    @Test
    @DisplayName("연결되지 않는 역을 즐겨찾기 등록하려고 하면 실패한다")
    void saveFavoriteOfNotLinkedSection() {
        // given
        when(stationService.findStationById(1L)).thenReturn(gangNam);
        when(stationService.findStationById(2L)).thenReturn(gyoDae);
        doThrow(NotLinkedPathException.class).when(pathService).validatePath(any(), any());

        // when
        assertThatIllegalArgumentException()
                .isThrownBy(() -> favoriteService.saveFavoriteOfMine(mond.getId(), favoriteRequest));
    }

    private void verifySourceAndTarget(StationResponse source, StationResponse target,
                                       Station expectedSource, Station expectedTarget) {
        assertAll(
                () -> assertThat(source.getId()).isEqualTo(expectedSource.getId()),
                () -> assertThat(source.getName()).isEqualTo(expectedSource.getName()),
                () -> assertThat(target.getId()).isEqualTo(expectedTarget.getId()),
                () -> assertThat(target.getName()).isEqualTo(expectedTarget.getName())
        );
    }
}
