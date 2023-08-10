package io.github.nickm980.smallville;

import io.github.nickm980.smallville.llm.ChatGPT;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import static org.mockito.Mockito.verify;

public class EndpointsTest {

    private final Context ctx = mock(Context.class);
    private final ChatGPT llm = mock(ChatGPT.class);

    @Test
    public void test_mockito_works() {
        when(ctx.queryParam("username")).thenReturn("Roland");
        ctx.status(200);
        verify(ctx).status(200);       
    }

}