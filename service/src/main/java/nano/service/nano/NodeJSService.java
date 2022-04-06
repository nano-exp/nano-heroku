package nano.service.nano;

import nano.support.Zx;
import org.springframework.stereotype.Service;

@Service
public class NodeJSService {

    private static final String NODE = "./www/node-*/bin/node";

    /**
     * Node random
     */
    public byte[] nodeRandom() {
        var SCREENSHOT_JS = "./www/scripts/random.js";
        var command = new String[]{"bash", "-c", "%s %s".formatted(NODE, SCREENSHOT_JS)};
        return Zx.$(command).join();
    }
}
