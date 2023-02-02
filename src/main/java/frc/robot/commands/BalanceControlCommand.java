package frc.robot.commands;

import frc.robot.Constants;
import frc.robot.Robot;
import frc.robot.RobotContainer;
import frc.robot.subsystems.SwerveDriveSubsystem;
import frc.robot.Constants.DriveConstants;


import edu.wpi.first.wpilibj2.command.CommandBase;

public class BalanceControlCommand extends CommandBase {
    
    private final SwerveDriveSubsystem swerveSubsystem;

    private double error;
    private double currentAngle;
    private double drivePower;

    public BalanceControlCommand() {
        this.swerveSubsystem = RobotContainer.swerveSubsystem;
        addRequirements(m_DriveSubsystem);
      }
      public BalanceOnBeamCommand() {
        this.m_DriveSubsystem = Robot.m_driveSubsystem;
        addRequirements(m_DriveSubsystem);
      }
    
      // Called when the command is initially scheduled.
      @Override
      public void initialize() {}
    
      // Called every time the scheduler runs while the command is scheduled.
      @Override
      public void execute() {
        // Uncomment the line below this to simulate the gyroscope axis with a controller joystick
        // Double currentAngle = -1 * Robot.controller.getRawAxis(Constants.LEFT_VERTICAL_JOYSTICK_AXIS) * 45;
        this.currentAngle = swerveSubsystem.getPitch();
    
        error = DriveConstants.balancingControlGoalDegrees - currentAngle;
        drivePower = -Math.min(DriveConstants.balancingControlDriveKP * error, 1);
    
        // Our robot needed an extra push to drive up in reverse, probably due to weight imbalances
        if (drivePower < 0) {
          drivePower *= DriveConstants.blancingControlBackwardsPowerMultiplier;
        }
    
        // Limit the max power
        if (Math.abs(drivePower) > 0.4) {
          drivePower = Math.copySign(0.4, drivePower);
        }
    
        m_DriveSubsystem.drive(drivePower, drivePower);
        
        // Debugging Print Statments
        System.out.println("Current Angle: " + currentAngle);
        System.out.println("Error " + error);
        System.out.println("Drive Power: " + drivePower);
      }
    
      // Called once the command ends or is interrupted.
      @Override
      public void end(boolean interrupted) {
        swerveSubsystem.stopModules();
      }
    
      // Returns true when the command should end.
      @Override
      public boolean isFinished() {
        return Math.abs(error) < DriveConstants.balancingControlTresholdDegrees; // End the command when we are within the specified threshold of being 'flat' (gyroscope pitch of 0 degrees)
      }
    }
