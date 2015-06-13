package edu.oregonstate.eecs.iis.avatolcv.ws.morphobank;

import java.util.List;

public class AnnotationInfo {
	
			
//{"ok":true,"annotations":[{"type":"polygon","points":[{"x":"31.891597158772733","y":"19.44466304661473"},{"x":"32.143102753789776","y":"26.495894651242097"},{"x":"39.436765009284095","y":"29.182078119671573"},{"x":"47.48494404982955","y":"32.875580388762096"},{"x":"51.76053916511931","y":"27.167440518349466"},{"x":"54.52710071030682","y":"18.437344245953675"},{"x":"50.50301119003409","y":"10.37879384066525"},{"x":"44.21537131460796","y":"6.013745704467354"}]},{"type":"polygon","points":[{"x":"46.73042726477841","y":"58.73009627239579"},{"x":"48.742472024914775","y":"55.37236693685895"},{"x":"51.00602238006818","y":"54.36504813619789"},{"x":"54.275595115289775","y":"53.69350226909053"},{"x":"56.79065106546023","y":"53.69350226909053"},{"x":"59.557212610647724","y":"54.70082106975158"},{"x":"61.82076296580114","y":"56.715458671073684"},{"x":"63.329796535903405","y":"58.73009627239579"},{"x":"64.33581891597159","y":"62.759371475040005"},{"x":"64.33581891597159","y":"66.78864667768421"},{"x":"63.58130213092045","y":"72.49678654809685"},{"x":"61.56925737078409","y":"78.54069935206317"},{"x":"58.048179040545456","y":"84.58461215602948"},{"x":"52.51505595017045","y":"85.59193095669055"},{"x":"45.47289928969318","y":"73.83987828231159"}]}]}
	 private String ok;
	 private List<MBAnnotation> annotations;
	 
	 public void setOk(String s){
		 this.ok = s;
	 }
	 public String getOk(){
		 return this.ok;
	 }
	 public void setAnnotations(List<MBAnnotation> s){
		 this.annotations = s;
	 }
	 public List<MBAnnotation> getAnnotations(){
		 return this.annotations;
	 }
	 
	 public static class MBAnnotation {
		 public static final String POLYGON = "polygon";
		 public static final String RECTANGLE = "rectangle";
		 public static final String POINT = "point";
		 //{"type":"polygon","points":[{"x":"31.891597158772733","y":"19.44466304661473"},{"x":"32.143102753789776","y":"26.495894651242097"},{"x":"39.436765009284095","y":"29.182078119671573"},{"x":"47.48494404982955","y":"32.875580388762096"},{"x":"51.76053916511931","y":"27.167440518349466"},{"x":"54.52710071030682","y":"18.437344245953675"},{"x":"50.50301119003409","y":"10.37879384066525"},{"x":"44.21537131460796","y":"6.013745704467354"}]}
		 private String type;
		 private List<MBAnnotationPoint> points;
		 
		 public void setType(String s){
			 this.type = s;
		 }
		 public String getType(){
			 return this.type;
		 }
		 
		 public void setPoints(List<MBAnnotationPoint> s){
			 this.points = s;
		 }
		 public List<MBAnnotationPoint> getPoints(){
			 return this.points;
		 }
	 }
	 
	 public static class MBAnnotationPoint {
		 //{"x":"31.891597158772733","y":"19.44466304661473"}
		 private double x;
		 private double y;
		 
		 public void setX(double s){
			 this.x = s;
		 }
		 public double getX(){
			 return this.x;
		 }
		 public void setY(double s){
			 this.y = s;
		 }
		 public double getY(){
			 return this.y;
		 }
	 }
}   

