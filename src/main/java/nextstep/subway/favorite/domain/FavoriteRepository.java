package nextstep.subway.favorite.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findAllByMemberIdAndDeletedFalse(Long memberId);
    Optional<Favorite> findByIdAndMemberId(Long favoriteId, Long id);
}
