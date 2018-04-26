package cjdetonator;
import robocode.*;
import java.awt.Color;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.Robot;
import robocode.ScannedRobotEvent;

public class Detonator extends Robot
{
	double movimento;
	double tiro = 1.5;
	int dano = 0;

	public void run() {
		// cores do robo
		setBodyColor(Color.red);
		setGunColor(Color.gray);
		setRadarColor(Color.yellow);
		setBulletColor(Color.green);
		setScanColor(Color.black);

		// pega as dimensoes do campo de batalha
		movimento = Math.max(getBattleFieldWidth(), getBattleFieldHeight());		

		// vira esquerda e vai pra parede
		turnLeft(getHeading() % 90);		
		ahead(movimento);			
		turnRight(90);
		
		while (true) {
			turnGunRight(360);
			fire(3.0); // sniper, atira parado
			ahead(movimento);
			turnRight(90);
		}
	}

	public void onScannedRobot(ScannedRobotEvent e) {
		
		// mira recebe posicao do inimigo
		mira(e.getBearing());

		// tiro final quando inimigo tem energia baixa
		if(e.getEnergy() < 20){
			if(getEnergy() > 50){
				fire(e.getEnergy());
			}else{
				fire(6.0);
			}
		}else{
			// verifica se o inimigo esta na mira e a distancia dele
			if(inimigoNaMira(e.getDistance())){
				// se a nossa energia estiver baixa, usa arma fraca
				if(getEnergy() < 30){
					fire(1);
				}else{ // se a energia estiver boa e estiver perto, usa arma mais pesada
					if(e.getDistance() < 100){
						fire(tiro * 2);
					}else{
						fire(tiro);
					}					
				}
			}else{
				// se tomar muito dano seguido, muda a direcao
				if(dano > 3){
					turnGunRight(270);
					turnRight(getHeading() % 90);
					fire(tiro);
					ahead(movimento);
					dano = 0;
				}				
			}
		}
	}

	public void onHitByBullet(HitByBulletEvent e) {
		// quando for acertado, usa armamento mais pesado
		tiro = 2.5;
		dano++;
	}
	
	public void onHitWall(HitWallEvent e) {
		
	}
	
	public void onHitRobot(HitRobotEvent e){
		fire(5.0);
	}

	// vira a arma continuamente enquanto detectar o inimigo
	public void mira(double posInimigo) {
		double posicionarCanhao = getHeading() + posInimigo - getGunHeading();

		// verifica se a arma vai girar no sentido horario ou antihorario
		if (!(posicionarCanhao > -180 && posicionarCanhao <= 180)) {
			while (posicionarCanhao <= -180) {
				posicionarCanhao += 360;
			}
			while (posicionarCanhao > 180) {
				posicionarCanhao -= 360;
			}
		}
		turnGunRight(posicionarCanhao);
	}

	// verifica se pode atirar no inimigo
	// se a arma estiver pronta e se o inimigo estiver perto
	public boolean inimigoNaMira(double distanciaInimigo){
		return(getGunHeat() == 0 && distanciaInimigo < 250);
	}
}
