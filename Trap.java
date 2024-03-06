package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkLowLevel.MotorType;
import com.revrobotics.SparkRelativeEncoder.Type;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.MecanumControllerCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;

public class Trap extends SubsystemBase {
    private final CANSparkMax m_shoulder = new CANSparkMax(15, MotorType.kBrushed);
    private final RelativeEncoder m_encoder = m_shoulder.getEncoder(Type.kQuadrature, 2048 * 4);

    private double target;
    private double m_speed= 0.0;
        
    

    public Trap() {
        m_shoulder.setInverted(true);
    }

    @Override
    public
    void periodic() {
    m_shoulder.set(m_speed);
    System.out.println(m_encoder.getPosition() + " " + target + " " + m_speed);
    }

    public boolean onTarget() {
        if (target > 0 && m_encoder.getPosition() > 0 || target < 0 && m_encoder.getPosition() < 0) {
            return Math.abs(m_encoder.getPosition() - target) <= 0.008;
        } else if (target < 0 && m_encoder.getPosition() > 0 || target > 0 && m_encoder.getPosition() < 0) {
            return Math.abs(m_encoder.getPosition() + target) <= 0.008;
        } else return false;
        // if (target > 0 ) {
        //     return Math.abs(m_encoder.getPosition()) - target <= 0.1 && Math.abs(m_encoder.getPosition()) - target >= -0.1;
        // } else
        // return Math.abs(m_encoder.getPosition()) + target <= 0.1 && Math.abs(m_encoder.getPosition()) + target >= -0.1;

    }

    public Command setSpeed(double speed) {
        if (target > m_encoder.getPosition()) {
            return this.runOnce(() -> m_speed = speed);
        } else if (target < m_encoder.getPosition()) {
            return this.runOnce(() -> m_speed -= speed);
        } else return this.runOnce(() -> m_speed = 0);
    }
    
    public Command moveTo(double position, double speed) {
        return Commands.sequence(
            this.runOnce(() -> target = position), 
            this.runOnce(() ->  {
            if (target > m_encoder.getPosition()) {
                m_speed = speed;
            } else if (target < m_encoder.getPosition()) {
               m_speed -= speed;
            } else m_speed = 0;
        }),
            new WaitUntilCommand(this::onTarget),
            this.runOnce(() -> m_speed = 0)
        );
    }
}
