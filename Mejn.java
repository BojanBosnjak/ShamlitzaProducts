package Ket;

public class Mejn {

	public static void main(String[] args) {
	//SviArtikli s = new SviArtikli ("jdbc:sqlite:/home/ubuntu/Desktop/Shamlitza/SviArtikliShamlitzaDatabase");
	
	//Desktop/sgn	
	//SviArtikli s = new SviArtikli ("jdbc:sqlite:/home/ubuntu/Desktop/sgn/SviArtikliShamlitzaDatabase");
	//s.connect();
	//s.pretragaPoSifriProizvoda();
	//s.pretragaPoImenuProizvoda();
	//s.pretragaPoDizajneru();
	//s.pretragaPoVrsti();
	//s.unesiProizvod();
	//s.sifre();
	//s.azuriranje();

	//s.disconnect();
	
	
	Troskovnik t = new Troskovnik("jdbc:sqlite:/home/ubuntu/Desktop/sgn/SviArtikliShamlitzaDatabase");
	t.connect();
	//t.unesiTrosak();
	//t.sviTroskovi();
	//t.troskoviMesecno();
	//t.pretragaTroskova();
	//t.pretragaTroskovaFaktura();
	t.disconnect();
		
	//Dobit d = new Dobit("jdbc:sqlite:/home/ubuntu/Desktop/sgn/SviArtikliShamlitzaDatabase");
	//d.connect();
	//d.prodatiArtikal();
	//d.prodatoMesecno();
	//d.razlikaUkupno();
	
	//d.disconnect();	
	}
	

}
