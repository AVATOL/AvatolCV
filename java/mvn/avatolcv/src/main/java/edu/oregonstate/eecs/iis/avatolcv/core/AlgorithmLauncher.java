package edu.oregonstate.eecs.iis.avatolcv.core;

public class AlgorithmLauncher {
	public static void main(String[] args){
		try {
			AlgorithmModules modules = new AlgorithmModules("C:\\avatol\\git\\modules");
			AlgorithmLauncher launcher = new AlgorithmLauncher();
			launcher.launch("segBogus1", modules);
		}
		catch(AvatolCVException e){
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}
	public AlgorithmLauncher(){
		
	}
	public void launch(String algName, AlgorithmModules modules){
		modules
	}
}
