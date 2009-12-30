package com.e2open.smi.rule.pac.loader.locationmonitor;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class DirectoryScanner {
	private String locationToScan = ".";

	public static final String NEW = "NEW";
	public static final String CHANGED = "CHANGED";
	public static final String DELETED = "DELETED";
	private ArrayList<File> previousFiles = new ArrayList<File>();
	private HashMap<String, Long> previousFileMap = new HashMap<String, Long>();
	private ArrayList<File> currentFiles = new ArrayList<File>();
	private Date lastRun = new Date();
	private FilenameFilter filter;

	@SuppressWarnings("unused")
	private DirectoryScanner() {
	}

	public DirectoryScanner(String locationToScan, FilenameFilter filter) {
		this.locationToScan = locationToScan;
		this.filter = filter;
	}

	public HashMap<String, ArrayList<File>> scan() {
		File directory = new File(locationToScan);
		File[] files;
		if (null == filter)
			files = directory.listFiles();
		else
			files = directory.listFiles(filter);
		if (null == files)
			files = new File[0];
		lastRun = new Date();
		currentFiles.clear();
		for (File file : files) {
			if (file.isFile())
				currentFiles.add(file);
		}

		ArrayList<File> newFiles = calcLeftDiff(currentFiles, previousFiles);
		ArrayList<File> potentialChangedFiles = calcIntersection(currentFiles, previousFiles);
		ArrayList<File> deletedFiles = calcLeftDiff(previousFiles, currentFiles);

		ArrayList<File> changedFiles = new ArrayList<File>();

		for (File currentFile : potentialChangedFiles) {
			Long previousLastModifed = previousFileMap.get(currentFile.getName());
			if (currentFile.lastModified() > previousLastModifed) {
				changedFiles.add(currentFile);
			}
		}

		Collections.sort(newFiles);
		Collections.sort(changedFiles);
		Collections.sort(deletedFiles);
		
		HashMap<String, ArrayList<File>> currentFileStatus = new HashMap<String, ArrayList<File>>();
		currentFileStatus.put(NEW, newFiles);
		currentFileStatus.put(CHANGED, changedFiles);
		currentFileStatus.put(DELETED, deletedFiles);

		previousFiles.clear();
		previousFiles.addAll(currentFiles);
		previousFileMap.clear();
		for (File file : previousFiles) {
			previousFileMap.put(file.getName(), Long.valueOf(file.lastModified()));
		}
		return currentFileStatus;
	}

	public Date getLastRun() {
		return lastRun;
	}

	private ArrayList<File> calcIntersection(ArrayList<File> list1, ArrayList<File> list2) {
		ArrayList<File> answer = new ArrayList<File>(list1);
		answer.retainAll(list2);
		return answer;
	}

	private ArrayList<File> calcLeftDiff(ArrayList<File> list1, ArrayList<File> list2) {
		ArrayList<File> answer = new ArrayList<File>(list1);
		answer.removeAll(list2);
		return answer;
	}

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		ScheduledExecutorService stpe = Executors.newSingleThreadScheduledExecutor();
		Runnable command = new Runnable() {
			DirectoryScanner a = new DirectoryScanner("src/com/e2open/smi/rule/pac/loader", null);

			public void run() {
				HashMap<String, ArrayList<File>> scanResults = a.scan();

				System.out.println("new=" + scanResults.get(NEW));
				System.out.println("changed=" + scanResults.get(CHANGED));
				System.out.println("deleted=" + scanResults.get(DELETED));
				System.out.println("----------------------");
			}
		};

		ScheduledFuture<?> sf = stpe.scheduleWithFixedDelay(command, 1, 5, TimeUnit.SECONDS);

		Thread.sleep(21 * 1000);
		System.out.println("sleep done");
		sf.cancel(true);
		stpe.shutdown();
	}
}
