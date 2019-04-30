import java.util.Random;
import java.util.Scanner;

public class Start {
	static int POCET_JEDINCOV = 100;
	static int POCET_GENERACII = 1500;
	static float POCET_MUTOVANYCH = 0.5f; //pocet krizenych = 1 - pocet_mutovanych NEMENIT!!!
	static float PRAVDEPODOBNOST_KRIZENIA_GENU = 50; // hodnota je v %
	static float PRAVDEPODOBNOST_MUTACIE = 100; //hodnota v %
	static int TYP_KRIZENIA = 1; // 0 -> Proporcionálna selekcia - ruleta / 1 -> SELEKCIA OHODNOTENIM
	private static Scanner vstup;
	static int X, Y, smer, predok, zmena_smeru = 0, fitnes, chyba_cesty = 0;
	static int[][] mapa_pomoc, cista_mapa;
	static int list = -1, pcervene, pzlte, ppomarancove;
	
	//funkcia sluzi na vypis mapy
	private static void vypis(int[][] mapa) {
		System.out.println("\n");
		for(int y = 0; y<Y ; y++) {
			for(int x = 0; x < X ; x++) {
				if(mapa[x][y] == 0)System.out.print("X ");
				else System.out.print(mapa[x][y] + " ");
				if(mapa[x][y]<=9 && mapa[x][y] >= 0)System.out.print(" ");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	//funkcia sluzi na kontrolu pola teda zistujem ci tam je prekazka ak ano menim smer a ak nie tak sa len posuniem
	private static void chod_este(int x, int posun_x, int y, int posun_y, int prechod, int pred,int cislo) {
		if((mapa_pomoc[x+posun_x][y+posun_y] != prechod  && mapa_pomoc[x+posun_x][y+posun_y] != list) || predok == (pred+2)%4) {
			if(smer == (predok + 1)%4 || smer == (predok - 1 + 4)%4) smer = (smer + 2)%4;
			else if(predok == 1 || predok == 0) smer = ((smer + 1) % 4);
			else smer = ((smer - 1 + 4) % 4);
			zmena_smeru++;
		}
		else {
			x+=posun_x;
			y+=posun_y;
			predok = pred;
			zmena_smeru = 0;
		}
		chod(x,y,cislo,prechod);
	}
	
	// funkcia nám sluzi na posuny avsak hlavna logika je vo funkcii chod_este() tato funkcia len zabezpecuje okraje mapy
	private static void chod(int x, int y, int cislo, int prechod) {
		if(mapa_pomoc[x][y] == prechod || mapa_pomoc[x][y] == list) {
			zmena_smeru = 0;
			fitnes++;
			if(prechod == 1) {
				if(mapa_pomoc[x][y] == -1) pzlte--;
				else if(mapa_pomoc[x][y] == -2)ppomarancove--;
				else if(mapa_pomoc[x][y] == -3)pcervene--;
				mapa_pomoc[x][y] = cislo;
				if(pzlte > 0) list = -1;
				if(ppomarancove > 0 && pzlte == 0) list = -2;
				if(pcervene > 0 && ppomarancove == 0 && pzlte == 0) list = -3;
			}
			else mapa_pomoc[x][y] = cista_mapa[x][y];
		}
		if((x == 0 && smer == 3 && predok != 1) || (y == 0 && smer == 0 && predok != 2) || (x == X-1 && smer == 1 && predok != 3) || (y == Y-1 && smer == 2 && predok != 0));
		else {
			if( (zmena_smeru >= 3) || (x == 0 && smer == 3 && predok == 1) || (y == 0 && smer == 0 && predok == 2) || (x == X-1 && smer == 1 && predok == 3) || (y == Y-1 && smer == 2 && predok == 0)) {
				chyba_cesty = 1;
			}else if(smer == 0) {
				chod_este(x, 0, y, -1, prechod, 0, cislo);
			}else if(smer == 1) {
				chod_este(x, 1, y, 0, prechod, 1, cislo);
			}else if(smer == 2) {
				chod_este(x, 0, y, 1, prechod, 2, cislo);
			}else if(smer == 3) {
				chod_este(x, -1, y, 0, prechod, 3, cislo);
			}
		}
	}
	
	//funkcia vybera jedincov proporcionalnou selekciou a nasledne ich aj krizi
	private static void proporcionalna_selekcia(Jedinec[] generacia ,int pocet_genov,Random rand,int  i) {
		int rodic;
		int spolu_fitnes = 0;
		int spolu_pomoc = 0;
		Jedinec krizenec = new Jedinec(pocet_genov);
		for(int r=0; r<POCET_JEDINCOV; r++) spolu_fitnes += generacia[r].getFitnes();
		if(spolu_fitnes > 0) {
			spolu_pomoc = rand.nextInt(spolu_fitnes);
			for( rodic=0; rodic<POCET_JEDINCOV; rodic++) {
				spolu_pomoc -= generacia[rodic].getFitnes();
				if(spolu_pomoc <= 0) break;
			}
			for(int r = 0; r < pocet_genov; r++) {
				krizenec.setGenI(generacia[rodic].getGenI(r), r);
			}
			spolu_pomoc = rand.nextInt(spolu_fitnes);
			for( rodic=0; rodic<POCET_JEDINCOV; rodic++) {
				spolu_pomoc -= generacia[rodic].getFitnes();
				if(spolu_pomoc <= 0) break;
			}
			for(int r = 0; r < pocet_genov; r++) {
				int porucha_krizenia = 0;
				if(rand.nextInt(100) < PRAVDEPODOBNOST_KRIZENIA_GENU) {
					for(int k = 0; k<pocet_genov; k++) {
						if(generacia[rodic].getGenI(r) == krizenec.getGenI(k)) {
							porucha_krizenia = 1;
							break;
						}
					}
					if(porucha_krizenia == 0)krizenec.setGenI(generacia[rodic].getGenI(r), r);
				}
				generacia[i].setGenI(krizenec.getGenI(r),r);
			}
		}
	}
	
	//funkcia vytvara selekciu ohodnotenim a nasledne ich aj krizi
	private static void selekcia_ohodnotenim(Jedinec[] generacia,int pocet_genov, Random rand) {
		for(int d = 0; d<POCET_JEDINCOV/2; d++) {
			for(int r = 0; r < pocet_genov; r++) {
				int porucha_krizenia = 0;
				if(rand.nextInt(100) < PRAVDEPODOBNOST_KRIZENIA_GENU) {
					for(int k = 0; k<pocet_genov; k++) {
						if(generacia[d].getGenI(r) == generacia[POCET_JEDINCOV - d - 1].getGenI(k)) {
							porucha_krizenia = 1;
							break;
						}
					}
					if(porucha_krizenia == 0)generacia[POCET_JEDINCOV - d - 1].setGenI(generacia[d].getGenI(r), r);
				}
			}
		}
	}
	
	//kontroluje ci dany gen (jeho vstup) je volny a ak sa zablokuje po ceste, znovu spusti dany gen s premazavanim aby dostal
	// mapu naspat do povodneho stavu
	private static void pust_chod(int x, int y, int i ,int kade) {
		smer = kade;
		predok = smer;
		zmena_smeru = 0;
		chyba_cesty = 0;
		int z,p,c,l;
		z = pzlte;
		p = ppomarancove;
		c = pcervene;
		l = list;
		int cx = x,cy = y;
		if(mapa_pomoc[x][y] == 1 || mapa_pomoc[x][y] == list) chod(x, y, i + 2, 1);
		if(chyba_cesty == 1) {
			smer = kade;
			predok = smer;
			zmena_smeru = 0;
			chyba_cesty = 0;
			list = -22;
			chod(cx, cy, 1, i + 2);
			pzlte = z;
			ppomarancove = p;
			pcervene = c;
			list = l;
			fitnes = 0;
		}
	}
	
	//hlavna funkcia ktora nacitava vstupe udaje a prechadza cez jedincov
	//nasledne spusta ich geny a kontroluje fitnes 
	public static void main(String[] args) {
		list = -1;
		Random rand = new Random();
		int kamene, riesenie = 0;
		int pocet_genov, fitnes_min, cele_pohrabane = 0;
		int fitnes_max = 0;
		int[] najlepsi_jedinec;
		vstup = new Scanner(System.in);
		System.out.println("Zadajte typ vyberu jedinca \n\t0 - Proporcionálna selekcia (ruleta)\n\t1 - SELEKCIA OHODNOTENIM");
		TYP_KRIZENIA = vstup.nextInt(); 
		System.out.print("Zadaj veklost mapy\nx:");
		X = vstup.nextInt(); 
		System.out.print("y:");
		Y = vstup.nextInt(); 
		int[][] mapa = new int[X][Y];
		cista_mapa = new int[X][Y];
		mapa_pomoc = new int[X][Y];
		for(int y = 0; y<Y ; y++) {
			for(int x = 0; x < X ; x++) {
				mapa[x][y] = 1;
				cista_mapa[x][y] = 1;
				mapa_pomoc[x][y] = 1;
			}
		}
		System.out.print("Zadaj pocet kamenov:");
		kamene = vstup.nextInt(); 
		fitnes_min = X*Y - kamene;
		cele_pohrabane = X*Y;
		pocet_genov = X+Y+kamene;
		najlepsi_jedinec = new int[pocet_genov];
		if(pocet_genov > X*Y) pocet_genov = X*Y;
		System.out.println("Kamen suradnice kamena v tvare 'x y' :");
		for(int i=1;i<=kamene;i++) {
			int x,y;
			System.out.print("Zadaj " + i + ". kamen:");
			x = vstup.nextInt(); 
			y = vstup.nextInt(); 
			mapa[x][y] = 0;
			mapa_pomoc[x][y] = 0;
			cista_mapa[x][y] = 0;
		}
		int zlte, pomarancove, cervene;
		System.out.print("Zadaj pocet zltych listov na mape:");
		zlte = vstup.nextInt(); 
		for(int i=0; i<zlte;i++) {
			int x,y;
			System.out.print("Zadaj " + i + ". zlty list v tvare 'x y' :");
			x = vstup.nextInt(); 
			y = vstup.nextInt(); 
			mapa[x][y] = -1;
			mapa_pomoc[x][y] = -1;
			cista_mapa[x][y] = -1;
		}
		System.out.print("Zadaj pocet pomarancovych listov na mape:");
		pomarancove = vstup.nextInt(); 
		for(int i=0; i<pomarancove;i++) {
			int x,y;
			System.out.print("Zadaj " + i + ". pomarancove list v tvare 'x y' :");
			x = vstup.nextInt(); 
			y = vstup.nextInt(); 
			mapa[x][y] = -2;
			mapa_pomoc[x][y] = -2;
			cista_mapa[x][y] = -2;
		}
		System.out.print("Zadaj pocet cervenych listov na mape:");
		cervene = vstup.nextInt(); 
		for(int i=0; i<cervene;i++) {
			int x,y;
			System.out.print("Zadaj " + i + ". cervene list v tvare 'x y' :");
			x = vstup.nextInt(); 
			y = vstup.nextInt(); 
			mapa[x][y] = -3;
			mapa_pomoc[x][y] = -3;
			cista_mapa[x][y] = -3;
		}
		vypis(mapa);
		Jedinec[] generacia = new Jedinec[POCET_JEDINCOV];
		for(int i=0;i<POCET_JEDINCOV;i++) {
			generacia[i] = new Jedinec(pocet_genov);
			generacia[i].random_napln(pocet_genov, X, Y);
		}
		
		for(int next_populacia = 0; next_populacia < POCET_GENERACII; next_populacia++) {
			int priemer = 0;
			for(int p=0;p<POCET_JEDINCOV;p++) {
				ppomarancove = pomarancove;
				pzlte = zlte;
				pcervene = cervene;
				list = -1;
				for(int i = 0; i < pocet_genov;i++) {
					int vchod = generacia[p].getGenI(i);
					fitnes = 0;
					if(vchod < X) pust_chod(vchod, 0, i, 2);
					else if(vchod < X+Y) pust_chod(X-1, vchod % X, i, 3);
					else if(vchod < 2*X+Y) pust_chod((X-1)-(vchod % (X+Y)), Y-1, i, 0);
					else pust_chod(0, (Y-1)-(vchod % (2*X+Y)), i, 1);
					generacia[p].setFitnes(generacia[p].getFitnes() + fitnes);
				}
				priemer +=  generacia[p].getFitnes();
				if(fitnes_min > generacia[p].getFitnes()) fitnes_min = generacia[p].getFitnes();
				if(fitnes_max < generacia[p].getFitnes()) {
					fitnes_max = generacia[p].getFitnes();
					riesenie = next_populacia;
					for(int q = 0; q < pocet_genov; q++)najlepsi_jedinec[q] = generacia[p].getGenI(q);
					for(int y = 0; y < Y ; y++) {
						for(int x = 0; x < X ; x++) {
							mapa[x][y] = mapa_pomoc[x][y];
							mapa_pomoc[x][y] = cista_mapa[x][y];
						}
					}
				}
				else for(int y = 0; y < Y ; y++)for(int x = 0; x < X ; x++) mapa_pomoc[x][y] = cista_mapa[x][y];
				if(fitnes_max == cele_pohrabane-kamene) {
					break;
				}
			}
			if(fitnes_max == cele_pohrabane - kamene) {
				System.out.println("Generacia:  " + next_populacia  + "   Max: " + fitnes_max + "   POHRABANE");
				break;
			}
			else {
				int pomocna;
				int[] gen_pomoc;
				for(int i = 0; i<POCET_JEDINCOV; i++) {
					for(int j=0; j<POCET_JEDINCOV - 1; j++) {
						if(generacia[j].getFitnes()<generacia[j+1].getFitnes()) {
							pomocna = generacia[j].getFitnes();
							gen_pomoc = generacia[j].getGen();
							generacia[j].setFitnes(generacia[j+1].getFitnes());
							generacia[j].setGen(generacia[j+1].getGen());
							generacia[j+1].setFitnes(pomocna);
							generacia[j+1].setGen(gen_pomoc);
						}
					}
				}
				System.out.println("Generacia:  " + next_populacia  + "   Max: " + generacia[0].getFitnes() + "   Min:  " + generacia[POCET_JEDINCOV - 1].getFitnes() + " AVG:  " + priemer / POCET_JEDINCOV);
				if(TYP_KRIZENIA == 0) {
					for(int i = (int)(POCET_JEDINCOV*POCET_MUTOVANYCH); i<POCET_JEDINCOV; i++)proporcionalna_selekcia(generacia ,pocet_genov, rand, i);
				}
				else selekcia_ohodnotenim(generacia ,pocet_genov, rand);
				for(int i = 0; i<(POCET_JEDINCOV*POCET_MUTOVANYCH); i++) {
					if(rand.nextInt(100)<PRAVDEPODOBNOST_MUTACIE) {
						int n = rand.nextInt(pocet_genov);
						int m = rand.nextInt(pocet_genov);
						pomocna = generacia[i].getGenI(n);
						generacia[i].setGenI(generacia[i].getGenI(m), n);
						generacia[i].setGenI(pomocna, m);
					}
				}
				for(int i = 0; i<POCET_JEDINCOV; i++) {generacia[i].setFitnes(0);}
			}
		}
		if(fitnes_max != 0) {
			System.out.println("\nGeny najlepsieho jedinca ktorý mal fitnes " + fitnes_max + " najdeneho v " + riesenie + " generacii:");
			for(int q = 0; q < pocet_genov; q++)System.out.print(najlepsi_jedinec[q] + " ");
		}
		vypis(mapa);
		
		
	}
}