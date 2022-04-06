package nano.service.domain.hls;

import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;

@Service
public class HlsService {

    private static final Map<String, String> channelMap = Map.of(
            "cctv6", "http://39.134.39.39/PLTV/88888888/224/3221226226/index.m3u8",
            "cctv13", "http://39.134.115.163:8080/PLTV/88888910/224/3221225638/index.m3u8"
    );

    public String getHlsUrl(String name) {
        return channelMap.get(name);
    }

    public Collection<String> getChannels() {
        return channelMap.keySet();
    }
}
