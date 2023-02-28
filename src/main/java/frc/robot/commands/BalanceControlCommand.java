package frc.robot.commands;

import frc.robot.Constants.BalanceConstants;
import frc.robot.subsystems.SwerveDriveSubsystem;
import frc.robot.Constants.DriveConstants;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class BalanceControlCommand extends CommandBase {
    
    private final SwerveDriveSubsystem swerveSubsystem;

    private double error;
    private double currentAngle;
    private double drivePower;

    public BalanceControlCommand(SwerveDriveSubsystem swerveSubsystem){
      this.swerveSubsystem = swerveSubsystem;
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
    
        error = BalanceConstants.kBalancingControlGoalDegrees - currentAngle;
        drivePower = -Math.min(BalanceConstants.kBalancingControlDriveKP * error, 1);
    
        // Our robot needed an extra push to drive up in reverse, probably due to weight imbalances
        if (drivePower < 0) {
          drivePower *= BalanceConstants.kBalancingControlBackwardsPowerMultiplier;
        }
    
        // Limit the max power
        if (Math.abs(drivePower) > 0.5) {
          drivePower = Math.copySign(0.5, drivePower);
        }
        
        // Construct desired chassis speeds
        ChassisSpeeds chassisSpeeds;
        
        chassisSpeeds = ChassisSpeeds.fromFieldRelativeSpeeds(
          drivePower, 0, 0, swerveSubsystem.getRotation2d());
    
        
        SwerveModuleState[] moduleStates = DriveConstants.kDriveKinematics.toSwerveModuleStates(chassisSpeeds);
        
        // Output each module states to wheels
        swerveSubsystem.setModuleStates(moduleStates);
        
        SmartDashboard.putNumber("Current Angle: ", currentAngle);
        SmartDashboard.putNumber("Error ", error);
        SmartDashboard.putNumber("Drive Power: ", drivePower);
      }
    
      // Called once the command ends or is interrupted.
      @Override
      public void end(boolean interrupted) {
        swerveSubsystem.stopModules();
      }
    
      // Returns true when the command should end.
      @Override
      public boolean isFinished() {
        // End the command when we are within the specified threshold of being 'flat' (gyroscope pitch of 0 degrees)
        return Math.abs(error) < BalanceConstants.kBalancingControlTresholdDegrees;
      }
    }
