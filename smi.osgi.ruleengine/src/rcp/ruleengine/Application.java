package rcp.ruleengine;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

import com.e2open.smi.rule.engine.CEPServer;

/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IApplication {
	public Object start(IApplicationContext context) throws Exception {

		System.out.println("Starting CEP Server");
// this is cheesy and should find a better way to do this.
		Thread.sleep(10 * 1000);
		while (CEPServer.getServer().isCepServerRunning()) {
			Thread.sleep(5 * 1000);
		}
		System.out.println("Stopping CEP Server");
		return IApplication.EXIT_OK;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	public void stop() {
		System.out.println("Application.stop()");
	}
}
