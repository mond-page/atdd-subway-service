package nextstep.subway.path.application;

import nextstep.subway.auth.domain.LoginMember;
import nextstep.subway.line.application.LineService;
import nextstep.subway.path.domain.Path;
import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;

@Service
public class PathService {
    private final StationService stationService;
    private final LineService lineService;
    private final PathFinder pathFinder;

    public PathService(StationService stationService, LineService lineService,
                       PathFinder pathFinder) {
        this.stationService = stationService;
        this.lineService = lineService;
        this.pathFinder = pathFinder;
    }

    public PathResponse searchShortestPath(LoginMember loginMember, Long source, Long target) {
        Station sourceStation = stationService.findStationById(source);
        Station targetStation = stationService.findStationById(target);

        pathFinder.init(lineService.findAllLines());
        Path resultPath = pathFinder.getDijkstraPath(sourceStation, targetStation);
        resultPath.calculateFee(loginMember);
        return resultPath.toPathResponse();
    }

    public void validatePath(Station source, Station target) {
        pathFinder.init(lineService.findAllLines());
        pathFinder.validatePath(source, target);
    }
}
