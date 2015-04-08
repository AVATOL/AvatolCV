package edu.oregonstate.eecs.iis.avatolcv.ws.morphobank;

import java.util.List;

public class TaxaInfo {
//{"ok":true,"taxa":[{"taxonID":"72002","taxonName":"Aetobatus"},{"taxonID":"255564","taxonName":"Testicus testing"},{"taxonID":"71967","taxonName":""},{"taxonID":"138348","taxonName":"Dodecaceria sp."},{"taxonID":"138349","taxonName":"Thelepus cincinnatus"},{"taxonID":"138350","taxonName":"Amphitritides harpa"},{"taxonID":"138351","taxonName":"Marenzelleria viridis"},{"taxonID":"138352","taxonName":"Polydora giardi"},{"taxonID":"138353","taxonName":"Augeneriella alata"},{"taxonID":"138354","taxonName":"Fabricinuda sp."},{"taxonID":"138355","taxonName":"Manayunkia athalassia"},{"taxonID":"138356","taxonName":"Novofabria labrus"},{"taxonID":"138357","taxonName":"Amphicorina mobilis"},{"taxonID":"138358","taxonName":"Amphiglen terebro"},{"taxonID":"138359","taxonName":"Bispira crassicornis"},{"taxonID":"138360","taxonName":"Bispira manicata"},{"taxonID":"138361","taxonName":"Bispira porifera"},{"taxonID":"138362","taxonName":"Bispira serrata"},{"taxonID":"138363","taxonName":"Branchiomma nt"},{"taxonID":"138364","taxonName":"Branchiomma nsw"},{"taxonID":"138365","taxonName":"Branchiomma bairdi"},{"taxonID":"138366","taxonName":"Branchiomma nigromaculata"},{"taxonID":"138367","taxonName":"Calcisabella piloseta"},{"taxonID":"138368","taxonName":"Chone sp."},{"taxonID":"138369","taxonName":"Dialychone perkinsi"},{"taxonID":"138370","taxonName":"Euchone limnicola"},{"taxonID":"138371","taxonName":"Euchone sp."},{"taxonID":"138372","taxonName":"Fabrisabella vasculosa"},{"taxonID":"138373","taxonName":"Megalomma sp. a"},{"taxonID":"138374","taxonName":"Myxicola sp."},{"taxonID":"138375","taxonName":"Notaulax nsw"},{"taxonID":"138376","taxonName":"Notaulax qld"},{"taxonID":"138377","taxonName":"Pseudopotamilla nt"},{"taxonID":"138378","taxonName":"Pseudopotamilla qld"},{"taxonID":"138379","taxonName":"Pseudopotamilla monoculata"},{"taxonID":"138380","taxonName":"Pseudopotamilla nsw"},{"taxonID":"138381","taxonName":"Eudistylia vancouveri"},{"taxonID":"138382","taxonName":"Schizobranchia insignis"},{"taxonID":"138383","taxonName":"Sabella spallanzanii"},{"taxonID":"138384","taxonName":"Sabella pavonina"},{"taxonID":"138385","taxonName":"Sabellastarte australiensis"},{"taxonID":"138386","taxonName":"Sabellastarte nt"},{"taxonID":"138387","taxonName":"Sabellastate vic"},{"taxonID":"138388","taxonName":"Sabellastarte indonesia"},{"taxonID":"138389","taxonName":"Stylomma palmatum"},{"taxonID":"138390","taxonName":"Myriochele sp."},{"taxonID":"138391","taxonName":"Owenia qld"},{"taxonID":"138392","taxonName":"Owenia fusiformis"},{"taxonID":"138393","taxonName":"Crucigera zygophora"},{"taxonID":"138394","taxonName":"Ditrupa arietina"},{"taxonID":"138395","taxonName":"Hydroides sp."},{"taxonID":"138396","taxonName":"Pomatoceros triqueter"},{"taxonID":"138397","taxonName":"Spirobranchus lima"},{"taxonID":"138398","taxonName":"Protolaeospira tricostalis"},{"taxonID":"138688","taxonName":"A. gomesii"},{"taxonID":"128957","taxonName":"\u2020Abelisauridae"},{"taxonID":"129044","taxonName":"Afrotis"},{"taxonID":"138400","taxonName":"\u2020Protula tubularia"},{"taxonID":"138406","taxonName":"Osedax frankpressi"},{"taxonID":"138405","taxonName":"Lamellibrachia columna"},{"taxonID":"138411","taxonName":"Scoloplos armiger"},{"taxonID":"138407","taxonName":"Ridgeia piscesae"},{"taxonID":"138408","taxonName":"Riftia pachyptila"},{"taxonID":"129102","taxonName":"Aegotheles"},{"taxonID":"138399","taxonName":"Protula paliata"},{"taxonID":"138404","taxonName":"Sabellaria alveolata"}]}
	private String ok;
	private List<MBTaxon> taxa;
	
	public void setOk(String s){
		this.ok = s;
	}
	public String getOk(){
		return this.ok;
	}
	
	public void setTaxa(List<MBTaxon> s){
		this.taxa = s;
	}
	public List<MBTaxon> getTaxa(){
		return this.taxa;
	}
	
	public static class MBTaxon {
		private String taxonID;
		private String taxonName;
		
		public void setTaxonID(String s){
			this.taxonID = s;
		}
		public String getTaxonID(){
			return this.taxonID;
		}
		
		public void setTaxonName(String s){
			this.taxonName = s;
		}
		public String getTaxonName(){
			return this.taxonName;
		}
		
	}
}
