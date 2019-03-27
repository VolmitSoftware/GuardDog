package com.volmit.guarddog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

import mortar.bukkit.plugin.Instance;
import mortar.bukkit.plugin.MortarPlugin;
import mortar.lang.collection.GList;

public class GuardDog extends MortarPlugin
{
	@Instance
	public static GuardDog instance;

	private BufferedReader reader;
	private Thread monitor;

	@Override
	public void start()
	{
		try
		{
			PipedOutputStream pipeOut = new PipedOutputStream();
			PipedInputStream stream = new PipedInputStream(pipeOut);
			System.setOut(new PrintStream(pipeOut));
			reader = new BufferedReader(new InputStreamReader(stream));
			monitor = new Thread(() -> monitor());
			monitor.start();
			l("GuardDog is now monitoring the console.");
		}

		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	private void monitor()
	{
		while(!Thread.interrupted())
		{
			try
			{
				Thread.sleep(1000);
				searchForExceptions();
			}

			catch(InterruptedException e)
			{
				l("Monitor Interrupted, Guard Dog is no longer monitoring the console.");
				break;
			}

			catch(IOException e)
			{
				continue;
			}
		}
	}

	private void searchForExceptions() throws IOException
	{
		GList<String> f = new GList<>();
		String l = "";
		String e = null;
		while((l = reader.readLine()) != null)
		{
			if(l.contains(" WARN]: "))
			{
				if(e == null)
				{
					e = "Recorded at " + l.split("\\Q WARN]: \\E")[0].replaceAll("\\Q[\\E", "") + "\n------------------------------------------------------------------\n";
				}

				e += l.split("\\Q WARN]: \\E")[1] + "\n";
			}

			else if(e != null)
			{
				f.add(e);
				e = null;
			}
		}
	}

	@Override
	public void stop()
	{

	}

	@Override
	public String getTag(String subTag)
	{
		return null;
	}

}
