package nextstep.subway.favorite.domain;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import nextstep.subway.BaseEntity;
import nextstep.subway.favorite.dto.FavoriteResponse;
import nextstep.subway.member.domain.Member;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

@Entity
public class Favorite extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "source_station_id")
    private Station source;

    @ManyToOne
    @JoinColumn(name = "target_station_id")
    private Station target;

    @Column
    private boolean deleted = false;

    protected Favorite() {
    }

    public Favorite(Member member, Station source, Station target) {
        this.member = member;
        this.source = source;
        this.target = target;
    }

    public void delete() {
        this.deleted = true;
    }

    public FavoriteResponse toFavoriteResponse() {
        StationResponse sourceResponse = StationResponse.of(this.source);
        StationResponse targetResponse = StationResponse.of(this.target);
        return new FavoriteResponse(this.id, sourceResponse, targetResponse);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Favorite favorite = (Favorite) o;
        return Objects.equals(id, favorite.id) && Objects.equals(member.getId(), favorite.member.getId())
                && Objects.equals(source.getId(), favorite.source.getId()) && Objects.equals(target.getId(),
                favorite.target.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, member.getId(), source.getId(), target.getId());
    }
}
