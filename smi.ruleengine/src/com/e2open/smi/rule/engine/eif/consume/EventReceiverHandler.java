package com.e2open.smi.rule.engine.eif.consume;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import com.e2open.smi.rule.engine.CEPServer;
import com.e2open.smi.rule.engine.eif.event.Event;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;


public class EventReceiverHandler extends IoHandlerAdapter {
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		 System.out.println(cause.getMessage());
         session.close(true);
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		Event event = (Event) message;
		if (event.getType().equals("SHUTDOWN")) {
			CEPServer.getServer().shutdownAndAwaitTermination();
		} else {
			// put the event into the CEP engine
			EPServiceProvider engine = EPServiceProviderManager.getDefaultProvider();
			EPRuntime runtimeEngine = engine.getEPRuntime();
			runtimeEngine.sendEvent(event);
		}
	}

    @Override
    public void sessionClosed(IoSession session) throws Exception {
            session.close(false); // close on flush
            super.sessionClosed(session);
    }
}
