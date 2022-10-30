package com.sunlit.client;

import com.sunlit.common.Request;
import com.sunlit.common.Response;

public interface RPCClient {
    Response sendRequest(Request request);
}
