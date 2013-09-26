/*
 * @(#)ExecutionManager.java	1.28 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * Copyright (c) 1997-1999 by Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 * 
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

package nd.com.sun.tools.example.debug.bdi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.SwingUtilities;

import nd.com.sun.tools.example.debug.event.JDIListener;
import nd.com.sun.tools.example.debug.expr.ExpressionParser;
import nd.com.sun.tools.example.debug.expr.ParseException;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Bootstrap;
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.InvocationException;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadGroupReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VirtualMachineManager;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.connect.ListeningConnector;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.ClassUnloadRequest;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.ExceptionRequest;
import com.sun.jdi.request.StepRequest;
import com.sun.jdi.request.ThreadDeathRequest;
import com.sun.jdi.request.ThreadStartRequest;

/**
 * Move this towards being only state and functionality that spans across
 * Sessions (and thus VMs).
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ExecutionManager {

	private Session session;

	/**
	 * Get/set JDI trace mode.
	 */
	int traceMode = VirtualMachine.TRACE_NONE;

	// //////////////// Listener registration //////////////////

	// Session Listeners

	Vector<SessionListener> sessionListeners = new Vector<SessionListener>();

	public void addSessionListener(SessionListener listener) {
		sessionListeners.add(listener);
	}

	public void removeSessionListener(SessionListener listener) {
		sessionListeners.remove(listener);
	}

	// Spec Listeners

	Vector<SpecListener> specListeners = new Vector<SpecListener>();

	public void addSpecListener(SpecListener cl) {
		specListeners.add(cl);
	}

	public void removeSpecListener(SpecListener cl) {
		specListeners.remove(cl);
	}

	// JDI Listeners

	Vector<JDIListener> jdiListeners = new Vector<JDIListener>();

	/**
	 * Adds a JDIListener
	 */
	public void addJDIListener(JDIListener jl) {
		jdiListeners.add(jl);
	}

	/**
	 * Adds a JDIListener - at the specified position
	 */
	public void addJDIListener(int index, JDIListener jl) {
		jdiListeners.add(index, jl);
	}

	/**
	 * Removes a JDIListener
	 */
	public void removeJDIListener(JDIListener jl) {
		jdiListeners.remove(jl);
	}

	// App Echo Listeners

	private Vector<OutputListener> appEchoListeners = new Vector<OutputListener>();

	public void addApplicationEchoListener(OutputListener l) {
		appEchoListeners.addElement(l);
	}

	public void removeApplicationEchoListener(OutputListener l) {
		appEchoListeners.removeElement(l);
	}

	// App Output Listeners

	private Vector<OutputListener> appOutputListeners = new Vector<OutputListener>();

	public void addApplicationOutputListener(OutputListener l) {
		appOutputListeners.addElement(l);
	}

	public void removeApplicationOutputListener(OutputListener l) {
		appOutputListeners.removeElement(l);
	}

	// App Error Listeners

	private Vector<OutputListener> appErrorListeners = new Vector<OutputListener>();

	public void addApplicationErrorListener(OutputListener l) {
		appErrorListeners.addElement(l);
	}

	public void removeApplicationErrorListener(OutputListener l) {
		appErrorListeners.removeElement(l);
	}

	// Diagnostic Listeners

	private Vector<OutputListener> diagnosticsListeners = new Vector<OutputListener>();

	public void addDiagnosticsListener(OutputListener l) {
		diagnosticsListeners.addElement(l);
	}

	public void removeDiagnosticsListener(OutputListener l) {
		diagnosticsListeners.removeElement(l);
	}

	// ///////// End Listener Registration //////////////

	// ### We probably don't want this public
	public VirtualMachine vm() {
		return session == null ? null : session.vm;
	}

	void ensureActiveSession() throws NoSessionException {
		if (session == null)
			throw new NoSessionException();
	}

	public EventRequestManager eventRequestManager() {
		return vm() == null ? null : vm().eventRequestManager();
	}

	/**
	 * Get JDI trace mode.
	 */
	public int getTraceMode(int mode) {
		return traceMode;
	}

	/**
	 * Set JDI trace mode.
	 */
	public void setTraceMode(int mode) {
		traceMode = mode;
		if (session != null) {
			session.setTraceMode(mode);
		}
	}

	/**
	 * Determine if VM is interrupted, i.e, present and not running.
	 */
	public boolean isInterrupted() /* should: throws NoSessionException */{
		// ensureActiveSession();
		return session.interrupted;
	}

	/**
	 * Return a list of ReferenceType objects for all currently loaded classes
	 * and interfaces. Array types are not returned.
	 */
	public List<ReferenceType> allClasses() throws NoSessionException {
		ensureActiveSession();
		return vm().allClasses();
	}

	/**
	 * Return a ReferenceType object for the currently loaded class or interface
	 * whose fully-qualified class name is specified, else return null if there
	 * is none.
	 * 
	 * In general, we must return a list of types, because multiple class
	 * loaders could have loaded a class with the same fully-qualified name.
	 */
	public List<ReferenceType> findClassesByName(String name)
			throws NoSessionException {
		ensureActiveSession();
		return vm().classesByName(name);
	}

	/**
	 * Return a list of ReferenceType objects for all currently loaded classes
	 * and interfaces whose name matches the given pattern. The pattern syntax
	 * is open to some future revision, but currently consists of a
	 * fully-qualified class name in which the first component may optionally be
	 * a "*" character, designating an arbitrary prefix.
	 */
	public List<ReferenceType> findClassesMatchingPattern(String pattern)
			throws NoSessionException {
		ensureActiveSession();
		List<ReferenceType> result = new ArrayList<ReferenceType>(); // ### Is
		// default
		// size
		// OK?
		if (pattern.startsWith("*.")) {
			// Wildcard matches any leading package name.
			pattern = pattern.substring(1);
			List<ReferenceType> classes = vm().allClasses();
			Iterator<ReferenceType> iter = classes.iterator();
			while (iter.hasNext()) {
				ReferenceType type = ((ReferenceType) iter.next());
				if (type.name().endsWith(pattern)) {
					result.add(type);
				}
			}
			return result;
		} else {
			// It's a class name.
			return vm().classesByName(pattern);
		}
	}

	/*
	 * Return a list of ThreadReference objects corresponding to the threads
	 * that are currently active in the VM. A thread is removed from the list
	 * just before the thread terminates.
	 */

	public List<ThreadReference> allThreads() throws NoSessionException {
		ensureActiveSession();
		return vm().allThreads();
	}

	/*
	 * Return a list of ThreadGroupReference objects corresponding to the
	 * top-level threadgroups that are currently active in the VM. Note that a
	 * thread group may be empty, or contain no threads as descendents.
	 */

	public List<ThreadGroupReference> topLevelThreadGroups()
			throws NoSessionException {
		ensureActiveSession();
		return vm().topLevelThreadGroups();
	}

	/*
	 * Return the system threadgroup.
	 */

	public ThreadGroupReference systemThreadGroup() throws NoSessionException {
		ensureActiveSession();
		return (ThreadGroupReference) vm().topLevelThreadGroups().get(0);
	}

	/*
	 * Evaluate an expression.
	 */

	public Value evaluate(final StackFrame f, String expr)
			throws ParseException, InvocationException, InvalidTypeException,
			ClassNotLoadedException, NoSessionException,
			IncompatibleThreadStateException {
		ExpressionParser.GetFrame frameGetter = null;
		ensureActiveSession();
		if (f != null) {
			frameGetter = new ExpressionParser.GetFrame() {
				public StackFrame get() /*
										 * throws
										 * IncompatibleThreadStateException
										 */{
					return f;
				}
			};
		}
		return ExpressionParser.evaluate(expr, vm(), frameGetter);
	}

	/*
	 * Start a new VM.
	 */

	public void run(boolean suspended, String vmArgs, String className,
			String args) throws VMLaunchFailureException {

		endSession();

		// ### Set a breakpoint on 'main' method.
		// ### Would be cleaner if we could just bring up VM already suspended.
		if (suspended) {
			// ### Set breakpoint at 'main(java.lang.String[])'.
			List<String> argList = new ArrayList<String>(1);
			argList.add("java.lang.String[]");
			createMethodBreakpoint(className, "main", argList);
		}

		String cmdLine = className + " " + args;

		startSession(new ChildSession(this, vmArgs, cmdLine, appInput,
				appOutput, appError, diagnostics));
	}

	/*
	 * Attach to an existing VM.
	 */
	public void attach(String portName) throws VMLaunchFailureException {
		endSession();

		// ### Changes made here for connectors have broken the
		// ### the 'Session' abstraction. The 'Session.attach()'
		// ### method is intended to encapsulate all of the various
		// ### ways in which session start-up can fail. (maddox 12/18/98)

		/*
		 * Now that attaches and launches both go through Connectors, it may be
		 * worth creating a new subclass of Session for attach sessions.
		 */
		VirtualMachineManager mgr = Bootstrap.virtualMachineManager();
		List<AttachingConnector> connectors = mgr.attachingConnectors();
		AttachingConnector connector = (AttachingConnector) connectors.get(0);
		Map<String, ?> arguments = connector.defaultArguments();
		((Connector.Argument) arguments.get("port")).setValue(portName);

		Session newSession = internalAttach(connector, arguments);
		if (newSession != null) {
			startSession(newSession);
		}
	}

	private Session internalAttach(AttachingConnector connector, Map arguments) {
		try {
			VirtualMachine vm = connector.attach(arguments);
			return new Session(vm, this, diagnostics);
		} catch (IOException ioe) {
			diagnostics.putString("\n Unable to attach to target VM: "
					+ ioe.getMessage());
		} catch (IllegalConnectorArgumentsException icae) {
			diagnostics.putString("\n Invalid connector arguments: "
					+ icae.getMessage());
		}
		return null;
	}

	private Session internalListen(ListeningConnector connector, Map arguments) {
		try {
			VirtualMachine vm = connector.accept(arguments);
			return new Session(vm, this, diagnostics);
		} catch (IOException ioe) {
			diagnostics
					.putString("\n Unable to accept connection to target VM: "
							+ ioe.getMessage());
		} catch (IllegalConnectorArgumentsException icae) {
			diagnostics.putString("\n Invalid connector arguments: "
					+ icae.getMessage());
		}
		return null;
	}

	/*
	 * Connect via user specified arguments
	 * 
	 * @return true on success
	 */
	public boolean explictStart(Connector connector, Map<String, ?> arguments)
			throws VMLaunchFailureException {
		Session newSession = null;

		endSession();

		if (connector instanceof LaunchingConnector) {
			// we were launched, use ChildSession
			newSession = new ChildSession(this, (LaunchingConnector) connector,
					arguments, appInput, appOutput, appError, diagnostics);
		} else if (connector instanceof AttachingConnector) {
			newSession = internalAttach((AttachingConnector) connector,
					arguments);
		} else if (connector instanceof ListeningConnector) {
			newSession = internalListen((ListeningConnector) connector,
					arguments);
		} else {
			diagnostics.putString("\n Unknown connector: " + connector);
		}
		if (newSession != null) {
			startSession(newSession);
		}
		return newSession != null;
	}

	/*
	 * Detach from VM. If VM was started by debugger, terminate it.
	 */
	public void detach() throws NoSessionException {
		ensureActiveSession();
		endSession();
	}

	private void startSession(Session s) throws VMLaunchFailureException {
		if (!s.attach()) {
			throw new VMLaunchFailureException();
		}
		session = s;
		EventRequestManager em = vm().eventRequestManager();
		ClassPrepareRequest classPrepareRequest = em
				.createClassPrepareRequest();
		// ### We must allow the deferred breakpoints to be resolved before
		// ### we continue executing the class. We could optimize if there
		// ### were no deferred breakpoints outstanding for a particular class.
		// ### Can we do this with JDI?
		classPrepareRequest.setSuspendPolicy(EventRequest.SUSPEND_ALL);
		classPrepareRequest.enable();
		ClassUnloadRequest classUnloadRequest = em.createClassUnloadRequest();
		classUnloadRequest.setSuspendPolicy(EventRequest.SUSPEND_NONE);
		classUnloadRequest.enable();
		ThreadStartRequest threadStartRequest = em.createThreadStartRequest();
		threadStartRequest.setSuspendPolicy(EventRequest.SUSPEND_NONE);
		threadStartRequest.enable();
		ThreadDeathRequest threadDeathRequest = em.createThreadDeathRequest();
		threadDeathRequest.setSuspendPolicy(EventRequest.SUSPEND_NONE);
		threadDeathRequest.enable();
		ExceptionRequest exceptionRequest = em.createExceptionRequest(null,
				false, true);
		exceptionRequest.setSuspendPolicy(EventRequest.SUSPEND_ALL);
		exceptionRequest.enable();
		validateThreadInfo();
		session.interrupted = true;
		notifySessionStart();
	}

	public void endSession() {
		if (session != null) {
			session.detach();
			session = null;
			invalidateThreadInfo();
			notifySessionDeath();
		}
	}

	/*
	 * Suspend all VM activity.
	 */

	public void interrupt() throws NoSessionException {
		ensureActiveSession();
		vm().suspend();
		// ### Is it guaranteed that the interrupt has happened?
		validateThreadInfo();
		session.interrupted = true;
		notifyInterrupted();
	}

	/*
	 * Resume interrupted VM.
	 */

	public void go() throws NoSessionException, VMNotInterruptedException {
		ensureActiveSession();
		invalidateThreadInfo();
		session.interrupted = false;
		notifyContinued();
		vm().resume();
	}

	/*
	 * Stepping.
	 */
	void clearPreviousStep(ThreadReference thread) {
		/*
		 * A previous step may not have completed on this thread; if so, it gets
		 * removed here.
		 */
		EventRequestManager mgr = vm().eventRequestManager();
		List<StepRequest> requests = mgr.stepRequests();
		Iterator<StepRequest> iter = requests.iterator();
		while (iter.hasNext()) {
			StepRequest request = (StepRequest) iter.next();
			if (request.thread().equals(thread)) {
				mgr.deleteEventRequest(request);
				break;
			}
		}
	}

	private void generalStep(ThreadReference thread, int size, int depth)
			throws NoSessionException {
		ensureActiveSession();
		invalidateThreadInfo();
		session.interrupted = false;
		notifyContinued();

		clearPreviousStep(thread);
		EventRequestManager reqMgr = vm().eventRequestManager();
		StepRequest request = reqMgr.createStepRequest(thread, size, depth);
		// We want just the next step event and no others
		request.addCountFilter(1);
		request.enable();
		vm().resume();
	}

	public void stepIntoInstruction(ThreadReference thread)
			throws NoSessionException {
		generalStep(thread, StepRequest.STEP_MIN, StepRequest.STEP_INTO);
	}

	public void stepOverInstruction(ThreadReference thread)
			throws NoSessionException {
		generalStep(thread, StepRequest.STEP_MIN, StepRequest.STEP_OVER);
	}

	public void stepIntoLine(ThreadReference thread) throws NoSessionException,
			AbsentInformationException {
		generalStep(thread, StepRequest.STEP_LINE, StepRequest.STEP_INTO);
	}

	public void stepOverLine(ThreadReference thread) throws NoSessionException,
			AbsentInformationException {
		generalStep(thread, StepRequest.STEP_LINE, StepRequest.STEP_OVER);
	}

	public void stepOut(ThreadReference thread) throws NoSessionException {
		generalStep(thread, StepRequest.STEP_MIN, StepRequest.STEP_OUT);
	}

	/*
	 * Thread control.
	 */

	public void suspendThread(ThreadReference thread) throws NoSessionException {
		ensureActiveSession();
		thread.suspend();
	}

	public void resumeThread(ThreadReference thread) throws NoSessionException {
		ensureActiveSession();
		thread.resume();
	}

	public void stopThread(ThreadReference thread) throws NoSessionException {
		ensureActiveSession();
		// ### Need an exception now. Which one to use?
		// thread.stop();
	}

	/*
	 * ThreadInfo objects -- Allow query of thread status and stack.
	 */

	private List<ThreadInfo> threadInfoList = new LinkedList<ThreadInfo>();
	// ### Should be weak! (in the value, not the key)
	private HashMap<ThreadReference, ThreadInfo> threadInfoMap = new HashMap<ThreadReference, ThreadInfo>();

	public ThreadInfo threadInfo(ThreadReference thread) {
		if (session == null || thread == null) {
			return null;
		}
		ThreadInfo info = (ThreadInfo) threadInfoMap.get(thread);
		if (info == null) {
			// ### Should not hardcode initial frame count and prefetch here!
			// info = new ThreadInfo(thread, 10, 10);
			info = new ThreadInfo(thread);
			if (session.interrupted) {
				info.validate();
			}
			threadInfoList.add(info);
			threadInfoMap.put(thread, info);
		}
		return info;
	}

	void validateThreadInfo() {
		session.interrupted = true;
		Iterator<ThreadInfo> iter = threadInfoList.iterator();
		while (iter.hasNext()) {
			((ThreadInfo) iter.next()).validate();
		}
	}

	protected void invalidateThreadInfo() {
		if (session != null) {
			session.interrupted = false;
			Iterator<ThreadInfo> iter = threadInfoList.iterator();
			while (iter.hasNext()) {
				((ThreadInfo) iter.next()).invalidate();
			}
		}
	}

	void removeThreadInfo(ThreadReference thread) {
		ThreadInfo info = (ThreadInfo) threadInfoMap.get(thread);
		if (info != null) {
			info.invalidate();
			threadInfoMap.remove(thread);
			threadInfoList.remove(info);
		}
	}

	/*
	 * Listen for Session control events.
	 */

	private void notifyInterrupted() {
		Vector<SessionListener> l = (Vector<SessionListener>) sessionListeners
				.clone();
		EventObject evt = new EventObject(this);
		for (int i = 0; i < l.size(); i++) {
			(l.elementAt(i)).sessionInterrupt(evt);
		}
	}

	private void notifyContinued() {
		Vector<SessionListener> l = (Vector<SessionListener>) sessionListeners
				.clone();
		EventObject evt = new EventObject(this);
		for (int i = 0; i < l.size(); i++) {
			(l.elementAt(i)).sessionContinue(evt);
		}
	}

	private void notifySessionStart() {
		Vector<SessionListener> l = (Vector<SessionListener>) sessionListeners
				.clone();
		EventObject evt = new EventObject(this);
		for (int i = 0; i < l.size(); i++) {
			(l.elementAt(i)).sessionStart(evt);
		}
	}

	private void notifySessionDeath() {
		/***
		 * noop for now Vector l = (Vector)sessionListeners.clone(); EventObject
		 * evt = new EventObject(this); for (int i = 0; i < l.size(); i++) {
		 * ((SessionListener)l.elementAt(i)).sessionDeath(evt); }
		 ****/
	}

	/*
	 * Listen for input and output requests from the application being debugged.
	 * These are generated only when the debuggee is spawned as a child of the
	 * debugger.
	 */

	private Object inputLock = new Object();
	private LinkedList<String> inputBuffer = new LinkedList<String>();

	/*
	 * private void resetInputBuffer() { synchronized (inputLock) { inputBuffer
	 * = new LinkedList<String>(); } }
	 */

	public void sendLineToApplication(String line) {
		synchronized (inputLock) {
			inputBuffer.addFirst(line);
			inputLock.notifyAll();
		}
	}

	private InputListener appInput = new InputListener() {
		public String getLine() {
			// Don't allow reader to be interrupted -- catch and retry.
			String line = null;
			while (line == null) {
				synchronized (inputLock) {
					try {
						while (inputBuffer.size() < 1) {
							inputLock.wait();
						}
						line = (String) inputBuffer.removeLast();
					} catch (InterruptedException e) {
					}
				}
			}
			// We must not be holding inputLock here, as the listener
			// that we call to echo a line might call us re-entrantly
			// to provide another line of input.
			// Run in Swing event dispatcher thread.
			final String input = line;
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					echoInputLine(input);
				}
			});
			return line;
		}
	};

	private static String newline = System.getProperty("line.separator");

	private void echoInputLine(String line) {
		Vector<OutputListener> l = (Vector<OutputListener>) appEchoListeners
				.clone();
		for (int i = 0; i < l.size(); i++) {
			OutputListener ol = (OutputListener) l.elementAt(i);
			ol.putString(line);
			ol.putString(newline);
		}
	}

	private OutputListener appOutput = new OutputListener() {
		public void putString(String string) {
			Vector<OutputListener> l = (Vector<OutputListener>) appOutputListeners
					.clone();
			for (int i = 0; i < l.size(); i++) {
				((OutputListener) l.elementAt(i)).putString(string);
			}
		}
	};

	private OutputListener appError = new OutputListener() {
		public void putString(String string) {
			Vector<OutputListener> l = (Vector<OutputListener>) appErrorListeners
					.clone();
			for (int i = 0; i < l.size(); i++) {
				((OutputListener) l.elementAt(i)).putString(string);
			}
		}
	};

	private OutputListener diagnostics = new OutputListener() {
		public void putString(String string) {
			Vector<OutputListener> l = (Vector<OutputListener>) diagnosticsListeners
					.clone();
			for (int i = 0; i < l.size(); i++) {
				((OutputListener) l.elementAt(i)).putString(string);
			}
		}
	};

	// /////////// Spec Request Creation/Deletion/Query ///////////

	private EventRequestSpecList specList = new EventRequestSpecList(this);

	public BreakpointSpec createSourceLineBreakpoint(String sourceName, int line) {
		return specList.createSourceLineBreakpoint(sourceName, line);
	}

	public BreakpointSpec createClassLineBreakpoint(String classPattern,
			int line) {
		return specList.createClassLineBreakpoint(classPattern, line);
	}

	public BreakpointSpec createMethodBreakpoint(String classPattern,
			String methodId, List<String> methodArgs) {
		return specList.createMethodBreakpoint(classPattern, methodId,
				methodArgs);
	}

	public ExceptionSpec createExceptionIntercept(String classPattern,
			boolean notifyCaught, boolean notifyUncaught) {
		return specList.createExceptionIntercept(classPattern, notifyCaught,
				notifyUncaught);
	}

	public AccessWatchpointSpec createAccessWatchpoint(String classPattern,
			String fieldId) {
		return specList.createAccessWatchpoint(classPattern, fieldId);
	}

	public ModificationWatchpointSpec createModificationWatchpoint(
			String classPattern, String fieldId) {
		return specList.createModificationWatchpoint(classPattern, fieldId);
	}

	public void delete(EventRequestSpec spec) {
		specList.delete(spec);
	}

	void resolve(ReferenceType refType) {
		specList.resolve(refType);
	}

	public void install(EventRequestSpec spec) {
		specList.install(spec, vm());
	}

	public List<EventRequestSpec> eventRequestSpecs() {
		return specList.eventRequestSpecs();
	}
}