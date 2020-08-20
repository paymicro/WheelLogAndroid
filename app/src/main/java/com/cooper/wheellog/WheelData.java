package com.cooper.wheellog;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Vibrator;

import com.cooper.wheellog.utils.Constants;
import com.cooper.wheellog.utils.Constants.ALARM_TYPE;
import com.cooper.wheellog.utils.Constants.WHEEL_TYPE;
import com.cooper.wheellog.utils.GotwayAdapter;
import com.cooper.wheellog.utils.InMotionAdapter;
import com.cooper.wheellog.utils.KingsongAdapter;
import com.cooper.wheellog.utils.NinebotAdapter;
import com.cooper.wheellog.utils.NinebotZAdapter;
import com.cooper.wheellog.utils.SettingsUtil;
import com.cooper.wheellog.utils.IWheelAdapter;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public class WheelData {
    private static final int TIME_BUFFER = 10;
    private static WheelData mInstance;
    private Timer ridingTimerControl;
    private BluetoothLeService mBluetoothLeService;

    private long graph_last_update_time;
    private static final int GRAPH_UPDATE_INTERVAL = 1000; // milliseconds
	private static final int RIDING_SPEED = 200; // 2km/h
    private ArrayList<String> xAxis = new ArrayList<>();
    private ArrayList<Float> currentAxis = new ArrayList<>();
    private ArrayList<Float> speedAxis = new ArrayList<>();
    // BMS1
    private String bms1SerialNumber = "";
    private String bms1VersionNumber = "";
    private int bms1FactoryCap = 0;
    private int bms1ActualCap = 0;
    private int bms1FullCycles = 0;
    private int bms1ChargeCount = 0;
    private String bms1MfgDateStr = "";
    private int bms1Status = 0;
    private int bms1RemCap = 0;
    private int bms1RemPerc = 0;
    private int bms1Current = 0;
    private int bms1Voltage = 0;
    private int bms1Temp1 = 0;
    private int bms1Temp2 = 0;
    private int bms1BalanceMap = 0;
    private int bms1Health = 0;
    private int bms1Cell1 = 0;
    private int bms1Cell2 = 0;
    private int bms1Cell3 = 0;
    private int bms1Cell4 = 0;
    private int bms1Cell5 = 0;
    private int bms1Cell6 = 0;
    private int bms1Cell7 = 0;
    private int bms1Cell8 = 0;
    private int bms1Cell9 = 0;
    private int bms1Cell10 = 0;
    private int bms1Cell11 = 0;
    private int bms1Cell12 = 0;
    private int bms1Cell13 = 0;
    private int bms1Cell14 = 0;
    private int bms1Cell15 = 0;
    private int bms1Cell16 = 0;

    // BMS2
    private String bms2SerialNumber = "";
    private String bms2VersionNumber = "";
    private int bms2FactoryCap = 0;
    private int bms2ActualCap = 0;
    private int bms2FullCycles = 0;
    private int bms2ChargeCount = 0;
    private String bms2MfgDateStr = "";
    private int bms2Status = 0;
    private int bms2RemCap = 0;
    private int bms2RemPerc = 0;
    private int bms2Current = 0;
    private int bms2Voltage = 0;
    private int bms2Temp1 = 0;
    private int bms2Temp2 = 0;
    private int bms2BalanceMap = 0;
    private int bms2Health = 0;
    private int bms2Cell1 = 0;
    private int bms2Cell2 = 0;
    private int bms2Cell3 = 0;
    private int bms2Cell4 = 0;
    private int bms2Cell5 = 0;
    private int bms2Cell6 = 0;
    private int bms2Cell7 = 0;
    private int bms2Cell8 = 0;
    private int bms2Cell9 = 0;
    private int bms2Cell10 = 0;
    private int bms2Cell11 = 0;
    private int bms2Cell12 = 0;
    private int bms2Cell13 = 0;
    private int bms2Cell14 = 0;
    private int bms2Cell15 = 0;
    private int bms2Cell16 = 0;
    //all
    private int mSpeed;
    private long mTotalDistance;
    private int mCurrent;
    private int mTemperature;
    private int mTemperature2;
    private double mAngle;
    private double mRoll;

    private int mMode;
    private int mBattery;
    private double mAverageBattery;
    private int mVoltage;
    private long mDistance;
    private long mUserDistance;
    private int mRideTime;
    private int mRidingTime;
    private int mLastRideTime;
    private int mTopSpeed;
    private int mVoltageSag;
    private int mFanStatus;
    private boolean mConnectionState = false;
    private boolean mNewWheelSettings = false;
    private String mName = "Unknown";
    private String mModel = "Unknown";
    private String mModeStr = "Unknown";
    private String mBtName = "";

    private String mAlert = "";

    private String mVersion = "";
    private String mSerialNumber = "Unknown";
    private WHEEL_TYPE mWheelType = WHEEL_TYPE.Unknown;
    private long rideStartTime;
    private long mStartTotalDistance;
	/// Wheel Settings
	private boolean mWheelLightEnabled = false;
	private boolean mWheelLedEnabled = false;
	private boolean mWheelButtonDisabled = false;
	private int mWheelMaxSpeed = 0;
	private int mWheelSpeakerVolume = 50;
	private int mWheelTiltHorizon = 0;
	
    private boolean mAlarmsEnabled = false;
    private boolean mDisablePhoneVibrate = false;
    private boolean mDisablePhoneBeep = false;
    private int mAlarm1Speed = 0;
    private int mAlarm2Speed = 0;
    private int mAlarm3Speed = 0;
    private int mAlarm1Battery = 0;
    private int mAlarm2Battery = 0;
    private int mAlarm3Battery = 0;
    private int mAlarmCurrent = 0;
    private int mAlarmTemperature = 0;

    private double mRotationSpeed = 70.0;
    private double mRotationVoltage = 84.00;
    private double mPowerFactor = 0.85;
    private double mAlarmFactor1 = 0.80;
    private double mAlarmFactor2 = 0.85;
    private double mAlarmFactor3 = 0.90;
    private int mAdvanceWarningSpeed = 0;

    private boolean mAlteredAlarms = false;
    private boolean mUseRatio = false;
    private boolean mBetterPercents = false;
    private boolean mSpeedAlarmExecuting = false;
    private boolean mCurrentAlarmExecuting = false;
	private boolean mTemperatureAlarmExecuting = false;
	private boolean mBmsView = false;
    private boolean mDataForLog = true;
    private boolean mVeteran = false;
    private String protoVer = "";

    private int duration = 1; // duration of sound
    private int sampleRate = 44100;//22050; // Hz (maximum frequency is 7902.13Hz (B8))
    private int numSamples = duration * sampleRate;
    private short buffer[] = new short[numSamples];
    private int sfreq = 440;
    
    private long timestamp_raw;
    private long timestamp_last;
    private static AudioTrack audioTrack = null;

    public IWheelAdapter getAdapter() {
        switch (mWheelType) {
            case GOTWAY:
                return GotwayAdapter.getInstance();
            case KINGSONG:
                return KingsongAdapter.getInstance();
            case NINEBOT:
                return NinebotAdapter.getInstance();
            case NINEBOT_Z:
                return NinebotZAdapter.getInstance();
            case INMOTION:
                return InMotionAdapter.getInstance();
            default:
                return null;
        }
    }

    void playBeep(ALARM_TYPE type) {
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, buffer.length,
                AudioTrack.MODE_STATIC);
        if (type.getValue()<4) {
            audioTrack.write(buffer, sampleRate / 20, ((type.getValue())*sampleRate) / 20); //50, 100, 150 ms depends on number of speed alarm

        } else if (type == ALARM_TYPE.CURRENT) {
            audioTrack.write(buffer, sampleRate *3 / 10, (2*sampleRate) / 20); //100 ms for current

        } else {
            audioTrack.write(buffer, sampleRate *3 / 10, (6*sampleRate) / 10); //300 ms temperature



        }

        //Timber.i("Beep: %d",(type.getValue()-1)*10*sampleRate / 50);
        audioTrack.play();

    }


    static void initiate() {
        if (mInstance == null)
            mInstance = new WheelData();
		else {
			if (mInstance.ridingTimerControl != null) {
				mInstance.ridingTimerControl.cancel();
				mInstance.ridingTimerControl = null;
			}
		}

        mInstance.full_reset();
        mInstance.prepareTone(mInstance.sfreq);
        mInstance.startRidingTimerControl();

    }

    private void prepareTone(int freq){

        for (int i = 0; i < numSamples; ++i)
        {
            double originalWave = Math.sin(2 * Math.PI * freq * i / sampleRate);
            double harmonic1 = 0.5 * Math.sin(2 * Math.PI * 2 * freq * i / sampleRate);
            double harmonic2 = 0.25 * Math.sin(2 * Math.PI * 4 * freq * i / sampleRate);
            double secondWave = Math.sin(2 * Math.PI * freq*1.34F * i / sampleRate);
            double thirdWave = Math.sin(2 * Math.PI * freq*2.0F * i / sampleRate);
            double fourthWave = Math.sin(2 * Math.PI * freq*2.68F * i / sampleRate);
            if (i<=(numSamples*3)/10) {
                buffer[i] = (short)((originalWave + harmonic1 + harmonic2)*(Short.MAX_VALUE)); //+ harmonic1 + harmonic2
            } else if (i<(numSamples*3)/5) {
                buffer[i] = (short)((originalWave + secondWave)*(Short.MAX_VALUE));
            } else {
                buffer[i] = (short)((thirdWave + fourthWave)*(Short.MAX_VALUE));
            }

        }

/*        for (int i = 0; i < 20*numSamples/50; ++i)
        {
            double originalWave = Math.sin(2 * Math.PI * freq * i / sampleRate);
            double harmonic1 = 0.5 * Math.sin(2 * Math.PI * 2 * freq * i / sampleRate);
            double harmonic2 = 0.25 * Math.sin(2 * Math.PI * 4 * freq * i / sampleRate);
            if ((i < 7*numSamples/50) || ((i > 9*numSamples/50) && (i < 11*numSamples/50)) || ((i > 15*numSamples/50) && (i < 16*numSamples/50)) || ((i > 17*numSamples/50) && (i < 18*numSamples/50)) || ((i > 19*numSamples/50) && (i < 20*numSamples/50))) {
                buffer[i] = (short)((originalWave )*Short.MAX_VALUE); //+ harmonic1 + harmonic2
            } else {buffer[i] = 0;}

        }
        for (int i = 20*numSamples/50; i < 25*numSamples/50; ++i)
        {
            if (i == 22*numSamples/50) {freq = (int)((double)freq * 1.5);};
            double originalWave = Math.sin(2 * Math.PI * freq * i / sampleRate);
            double harmonic1 = 0.5 * Math.sin(2 * Math.PI * 2 * freq * i / sampleRate);
            double harmonic2 = 0.25 * Math.sin(2 * Math.PI * 4 * freq * i / sampleRate);
            buffer[i] = (short)((originalWave + harmonic1 + harmonic2)*Short.MAX_VALUE);

        }
        for (int i = 25*numSamples/50; i < numSamples; ++i)
        {
            freq = freq +1;
            double originalWave = Math.sin(2 * Math.PI * freq * i / sampleRate);
            double harmonic1 = 0.5 * Math.sin(2 * Math.PI * 2 * freq * i / sampleRate);
            double harmonic2 = 0.25 * Math.sin(2 * Math.PI * 4 * freq * i / sampleRate);
            buffer[i] = (short)((originalWave + harmonic1 + harmonic2)*Short.MAX_VALUE);

        }  */
    }

	
	public void startRidingTimerControl() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (mConnectionState && (mSpeed > RIDING_SPEED)) mRidingTime += 1;
            }
        };
        ridingTimerControl = new Timer();
        ridingTimerControl.scheduleAtFixedRate(timerTask, 0, 1000);
    }




    public static WheelData getInstance() {
        return mInstance;
    }

    public int getSpeed() {
        return (int)Math.round(mSpeed / 10.0);
    }
	
	boolean getWheelLight() {
        return mWheelLightEnabled;
    }
	
	boolean getWheelLed() {
        return mWheelLedEnabled;
    }
	
	boolean getWheelHandleButton() {
        return mWheelButtonDisabled;
    }
	
    public int getWheelMaxSpeed() {
        return mWheelMaxSpeed;
    }

    int getSpeakerVolume() {
        return mWheelSpeakerVolume;
    }

    int getPedalsPosition() {
        return mWheelTiltHorizon;
    }

    public void setBtName(String btName) {
        mBtName = btName;
    }

    public void updateLight(boolean enabledLight) {
        if (mWheelLightEnabled != enabledLight) {
            mWheelLightEnabled = enabledLight;
            InMotionAdapter.getInstance().setLightState(enabledLight);
        }
    }

    public void updateLed(boolean enabledLed) {
        if (mWheelLedEnabled != enabledLed) {
            mWheelLedEnabled = enabledLed;
            InMotionAdapter.getInstance().setLedState(enabledLed);
        }
    }

    public void updateStrobe(int strobeMode) {
        if (mWheelType == WHEEL_TYPE.KINGSONG) {
            byte[] data = new byte[20];
            data[0] = (byte) 0xAA;
            data[1] = (byte) 0x55;
            data[2] = (byte) strobeMode;
            data[16] = (byte) 0x53;
            data[17] = (byte) 0x14;
            data[18] = (byte) 0x5A;
            data[19] = (byte) 0x5A;
            mBluetoothLeService.writeBluetoothGattCharacteristic(data);
        }
    }

    public void updateLedMode(int ledMode) {
        if (mWheelType == WHEEL_TYPE.KINGSONG) {
            byte[] data = new byte[20];
            data[0] = (byte) 0xAA;
            data[1] = (byte) 0x55;
            data[2] = (byte) ledMode;
            data[16] = (byte) 0x6C;
            data[17] = (byte) 0x14;
            data[18] = (byte) 0x5A;
            data[19] = (byte) 0x5A;
            mBluetoothLeService.writeBluetoothGattCharacteristic(data);
        }
    }

    public void updateAlarmMode(int alarmMode) {
        if (mWheelType == WHEEL_TYPE.GOTWAY) {
            switch (alarmMode) {
                case 0:
                    mBluetoothLeService.writeBluetoothGattCharacteristic("u".getBytes());
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mBluetoothLeService.writeBluetoothGattCharacteristic("b".getBytes());
                        }
                    }, 100);

                    break;
                case 1:
                    mBluetoothLeService.writeBluetoothGattCharacteristic("i".getBytes());
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mBluetoothLeService.writeBluetoothGattCharacteristic("b".getBytes());
                        }
                    }, 100);
                    break;
                case 2:
                    mBluetoothLeService.writeBluetoothGattCharacteristic("o".getBytes());
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mBluetoothLeService.writeBluetoothGattCharacteristic("b".getBytes());
                        }
                    }, 100);
                    break;
            }
        }
    }

    public void updateCalibration() {
        if (mWheelType == WHEEL_TYPE.GOTWAY) {
            //mBluetoothLeService.writeBluetoothGattCharacteristic("b".getBytes());
            mBluetoothLeService.writeBluetoothGattCharacteristic("c".getBytes());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothLeService.writeBluetoothGattCharacteristic("y".getBytes());
                }
            }, 300);
        }
    }

    public void updateHandleButton(boolean enabledButton) {
        if (mWheelButtonDisabled != enabledButton) {
            mWheelButtonDisabled = enabledButton;
            InMotionAdapter.getInstance().setHandleButtonState(enabledButton);
        }
    }

    public void updateSpeakerVolume(int speakerVolume) {
        if (mWheelSpeakerVolume != speakerVolume) {
            mWheelSpeakerVolume = speakerVolume;
            InMotionAdapter.getInstance().setSpeakerVolumeState(speakerVolume);
        }
    }

    public void updatePedals(int pedalAdjustment) {
        if (mWheelTiltHorizon != pedalAdjustment) {
            mWheelTiltHorizon = pedalAdjustment;
            InMotionAdapter.getInstance().setTiltHorizon(pedalAdjustment);
        }
    }

    public int getTemperature() {
        return mTemperature / 100;
    }

    public int getTemperature2() {
        return mTemperature2 / 100;
    }

    public double getAngle() {
        return mAngle;
    }

    public double getRoll() {
        return mRoll;
    }

    public int getBatteryLevel() {
        return mBattery;
    }

    int getFanStatus() {
        return mFanStatus;
    }

    boolean isConnected() {
        return mConnectionState;
    }

    String getVersion() {
        return mVersion;
    }

    int getMode() {
        return mMode;
    }

    WHEEL_TYPE getWheelType() {
        return mWheelType;
    }

    boolean isVeteran() {
        return mVeteran;
    }

    String getName() {
        return mName;
    }

    public String getModel() {
        return mModel;
    }

    String getModeStr() {
        return mModeStr;
    }

    String getAlert() {
        String nAlert = mAlert;
        mAlert = "";
        return nAlert;
    }

    String getSerial() {
        return mSerialNumber;
    }

    int getRideTime() {
        return mRideTime;
    }

    double getAverageSpeedDouble() {
        if (mTotalDistance != 0 && mRideTime != 0) {
            return (((mTotalDistance - mStartTotalDistance) * 3.6) / (mRideTime + mLastRideTime));
        } else return 0.0;
    }

    double getAverageRidingSpeedDouble() {
        if (mTotalDistance != 0 && mRidingTime != 0) {
            return (((mTotalDistance - mStartTotalDistance) * 3.6) / mRidingTime);
        } else return 0.0;
    }

    String getRideTimeString() {
        int currentTime = mRideTime + mLastRideTime;
        long hours = TimeUnit.SECONDS.toHours(currentTime);
        long minutes = TimeUnit.SECONDS.toMinutes(currentTime) -
                TimeUnit.HOURS.toMinutes(TimeUnit.SECONDS.toHours(currentTime));
        long seconds = TimeUnit.SECONDS.toSeconds(currentTime) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(currentTime));
        return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds);
    }

    String getRidingTimeString() {
        long hours = TimeUnit.SECONDS.toHours(mRidingTime);
        long minutes = TimeUnit.SECONDS.toMinutes(mRidingTime) -
                TimeUnit.HOURS.toMinutes(TimeUnit.SECONDS.toHours(mRidingTime));
        long seconds = TimeUnit.SECONDS.toSeconds(mRidingTime) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(mRidingTime));
        return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds);
    }

    double getSpeedDouble() {
        return mSpeed / 100.0;
    }

    double getVoltageDouble() {
        return mVoltage / 100.0;
    }

    double getVoltageSagDouble() {
        return mVoltageSag / 100.0;
    }

    double getPowerDouble() {
        return (mCurrent * mVoltage) / 10000.0;
    }

    double getCurrentDouble() {
        return mCurrent / 100.0;
    }

    int getTopSpeed() {
        return mTopSpeed;
    }

    double getTopSpeedDouble() {
        return mTopSpeed / 100.0;
    }

    int getDistance() {
        return (int) (mTotalDistance - mStartTotalDistance);
    }

    int getAlarm() {
        int alarm = 0;
        if (mSpeedAlarmExecuting) {
            alarm = alarm | 0x01;
        }
        if (mTemperatureAlarmExecuting) {
            alarm = alarm | 0x04;
        }
        if (mCurrentAlarmExecuting) {
            alarm = alarm | 0x02;
        }
        return alarm;
    }

    public double getWheelDistanceDouble() {
        return mDistance / 1000.0;
    }

    public BluetoothLeService getBluetoothLeService() {
        return mBluetoothLeService;
    }

    public double getUserDistanceDouble() {
        if (mUserDistance == 0 && mTotalDistance != 0) {
            Context mContext = mBluetoothLeService.getApplicationContext();
            mUserDistance = SettingsUtil.getUserDistance(mContext, mBluetoothLeService.getBluetoothDeviceAddress());
            if (mUserDistance == 0) {
                SettingsUtil.setUserDistance(mContext, mBluetoothLeService.getBluetoothDeviceAddress(), mTotalDistance);
                mUserDistance = mTotalDistance;
            }
        }
        return (mTotalDistance - mUserDistance) / 1000.0;
    }

    public String getMac() {
        return mBluetoothLeService.getBluetoothDeviceAddress();
    }

    public long getTimeStamp() {
        return timestamp_last;
    }

    public void resetUserDistance() {
        if (mTotalDistance != 0) {
            Context mContext = mBluetoothLeService.getApplicationContext();
            SettingsUtil.setUserDistance(mContext, mBluetoothLeService.getBluetoothDeviceAddress(), mTotalDistance);
            mUserDistance = mTotalDistance;
        }
    }

    public void resetTopSpeed() {
        mTopSpeed = 0;
    }

    public void resetVoltageSag() {
        Timber.i("Sag WD");
        mVoltageSag = 20000;
    }

    public void setBetterPercents(boolean betterPercents) {
        mBetterPercents = betterPercents;
        if (mWheelType == WHEEL_TYPE.INMOTION)
            InMotionAdapter.getInstance().setBetterPercents(betterPercents);
    }

    public double getDistanceDouble() {
        return (mTotalDistance - mStartTotalDistance) / 1000.0;
    }

    double getTotalDistanceDouble() {
        return mTotalDistance / 1000.0;
    }

    public long getTotalDistance() {
        return mTotalDistance;
    }

    public String getBms1SerialNumber() { return bms1SerialNumber;}
    public String getBms1VersionNumber() { return bms1VersionNumber;}
    public int getBms1FactoryCap() {return bms1FactoryCap;}
    public int getBms1ActualCap() {return bms1ActualCap;}
    public int getBms1FullCycles() {return bms1FullCycles;}
    public int getBms1ChargeCount() {return bms1ChargeCount;}
    public String getBms1MfgDateStr() {return bms1MfgDateStr;}
    public int getBms1Status() {return bms1Status;}
    public int getBms1RemCap() {return bms1RemCap;}
    public int getBms1RemPerc() {return bms1RemPerc;}
    public double getBms1Current() {return bms1Current/100.0;}
    public double getBms1Voltage() {return bms1Voltage/100.0;}
    public int getBms1Temp1() {return bms1Temp1;}
    public int getBms1Temp2() {return bms1Temp2;}
    public int getBms1BalanceMap() {return bms1BalanceMap;}
    public int getBms1Health() {return bms1Health;}
    public double getBms1Cell1() {return bms1Cell1/1000.0;}
    public double getBms1Cell2() {return bms1Cell2/1000.0;}
    public double getBms1Cell3() {return bms1Cell3/1000.0;}
    public double getBms1Cell4() {return bms1Cell4/1000.0;}
    public double getBms1Cell5() {return bms1Cell5/1000.0;}
    public double getBms1Cell6() {return bms1Cell6/1000.0;}
    public double getBms1Cell7() {return bms1Cell7/1000.0;}
    public double getBms1Cell8() {return bms1Cell8/1000.0;}
    public double getBms1Cell9() {return bms1Cell9/1000.0;}
    public double getBms1Cell10() {return bms1Cell10/1000.0;}
    public double getBms1Cell11() {return bms1Cell11/1000.0;}
    public double getBms1Cell12() {return bms1Cell12/1000.0;}
    public double getBms1Cell13() {return bms1Cell13/1000.0;}
    public double getBms1Cell14() {return bms1Cell14/1000.0;}
    public double getBms1Cell15() {return bms1Cell15/1000.0;}
    public double getBms1Cell16() {return bms1Cell16/1000.0;}
    public String getbms2SerialNumber() { return bms2SerialNumber;}
    public String getbms2VersionNumber() { return bms2VersionNumber;}
    public int getbms2FactoryCap() {return bms2FactoryCap;}
    public int getbms2ActualCap() {return bms2ActualCap;}
    public int getbms2FullCycles() {return bms2FullCycles;}
    public int getbms2ChargeCount() {return bms2ChargeCount;}
    public String getbms2MfgDateStr() {return bms2MfgDateStr;}
    public int getbms2Status() {return bms2Status;}
    public int getbms2RemCap() {return bms2RemCap;}
    public int getbms2RemPerc() {return bms2RemPerc;}
    public double getbms2Current() {return bms2Current/100.0;}
    public double getbms2Voltage() {return bms2Voltage/100.0;}
    public int getbms2Temp1() {return bms2Temp1;}
    public int getbms2Temp2() {return bms2Temp2;}
    public int getbms2BalanceMap() {return bms2BalanceMap;}
    public int getbms2Health() {return bms2Health;}
    public double getbms2Cell1() {return bms2Cell1/1000.0;}
    public double getbms2Cell2() {return bms2Cell2/1000.0;}
    public double getbms2Cell3() {return bms2Cell3/1000.0;}
    public double getbms2Cell4() {return bms2Cell4/1000.0;}
    public double getbms2Cell5() {return bms2Cell5/1000.0;}
    public double getbms2Cell6() {return bms2Cell6/1000.0;}
    public double getbms2Cell7() {return bms2Cell7/1000.0;}
    public double getbms2Cell8() {return bms2Cell8/1000.0;}
    public double getbms2Cell9() {return bms2Cell9/1000.0;}
    public double getbms2Cell10() {return bms2Cell10/1000.0;}
    public double getbms2Cell11() {return bms2Cell11/1000.0;}
    public double getbms2Cell12() {return bms2Cell12/1000.0;}
    public double getbms2Cell13() {return bms2Cell13/1000.0;}
    public double getbms2Cell14() {return bms2Cell14/1000.0;}
    public double getbms2Cell15() {return bms2Cell15/1000.0;}
    public double getbms2Cell16() {return bms2Cell16/1000.0;}



    public void setBmsView(boolean bmsView){
        if (mBmsView != bmsView) resetBmsData();
        mBmsView = bmsView;
    }

    public void resetBmsData() {
        // BMS1
        bms1SerialNumber = "";
        bms1VersionNumber = "";
        bms1FactoryCap = 0;
        bms1ActualCap = 0;
        bms1FullCycles = 0;
        bms1ChargeCount = 0;
        bms1MfgDateStr = "";
        bms1Status = 0;
        bms1RemCap = 0;
        bms1RemPerc = 0;
        bms1Current = 0;
        bms1Voltage = 0;
        bms1Temp1 = 0;
        bms1Temp2 = 0;
        bms1BalanceMap = 0;
        bms1Health = 0;
        bms1Cell1 = 0;
        bms1Cell2 = 0;
        bms1Cell3 = 0;
        bms1Cell4 = 0;
        bms1Cell5 = 0;
        bms1Cell6 = 0;
        bms1Cell7 = 0;
        bms1Cell8 = 0;
        bms1Cell9 = 0;
        bms1Cell10 = 0;
        bms1Cell11 = 0;
        bms1Cell12 = 0;
        bms1Cell13 = 0;
        bms1Cell14 = 0;
        bms1Cell15 = 0;
        bms1Cell16 = 0;

        // BMS2
        bms2SerialNumber = "";
        bms2VersionNumber = "";
        bms2FactoryCap = 0;
        bms2ActualCap = 0;
        bms2FullCycles = 0;
        bms2ChargeCount = 0;
        bms2MfgDateStr = "";
        bms2Status = 0;
        bms2RemCap = 0;
        bms2RemPerc = 0;
        bms2Current = 0;
        bms2Voltage = 0;
        bms2Temp1 = 0;
        bms2Temp2 = 0;
        bms2BalanceMap = 0;
        bms2Health = 0;
        bms2Cell1 = 0;
        bms2Cell2 = 0;
        bms2Cell3 = 0;
        bms2Cell4 = 0;
        bms2Cell5 = 0;
        bms2Cell6 = 0;
        bms2Cell7 = 0;
        bms2Cell8 = 0;
        bms2Cell9 = 0;
        bms2Cell10 = 0;
        bms2Cell11 = 0;
        bms2Cell12 = 0;
        bms2Cell13 = 0;
        bms2Cell14 = 0;
        bms2Cell15 = 0;
        bms2Cell16 = 0;
    }

    ArrayList<String> getXAxis() {
        return xAxis;
    }

    ArrayList<Float> getCurrentAxis() {
        return currentAxis;
    }

    ArrayList<Float> getSpeedAxis() {
        return speedAxis;
    }

    void setConnected(boolean connected) {
        mConnectionState = connected;
        Timber.i("State %b", connected);
    }

    void setAlarmsEnabled(boolean enabled) {
        mAlarmsEnabled = enabled;
    }

    void setUseRatio(boolean enabled) {
        mUseRatio = enabled;
    }

    void setPreferences(int alarm1Speed, int alarm1Battery,
                                   int alarm2Speed, int alarm2Battery,
                                   int alarm3Speed, int alarm3Battery,
                                   int alarmCurrent,int alarmTemperature,
                                    boolean disablePhoneVibrate, boolean disablePhoneBeep,
                                    boolean alteredAlarms, int rotationSpeed, int rotationVoltage,
                                    int powerFactor, int alarmFactor1, int alarmFactor2, int alarmFactor3, int warningSpeed
                                                                                                ) {
        mAlarm1Speed = alarm1Speed * 100;
        mAlarm2Speed = alarm2Speed * 100;
        mAlarm3Speed = alarm3Speed * 100;
        mAlarm1Battery = alarm1Battery;
        mAlarm2Battery = alarm2Battery;
        mAlarm3Battery = alarm3Battery;
        mAlarmCurrent = alarmCurrent * 100;
        mAlarmTemperature = alarmTemperature * 100;
        mDisablePhoneVibrate = disablePhoneVibrate;
        mDisablePhoneBeep = disablePhoneBeep;
        mAlteredAlarms = alteredAlarms;
        mRotationSpeed = (float)rotationSpeed/10.0;
        mRotationVoltage = (float)rotationVoltage/10.0;
        mPowerFactor = (float)powerFactor/100.0;
        mAlarmFactor1 = (float)alarmFactor1/100.0;
        mAlarmFactor2 = (float)alarmFactor2/100.0;
        mAlarmFactor3 = (float)alarmFactor3/100.0;
        mAdvanceWarningSpeed = warningSpeed;
    }

    private int byteArrayInt2(byte low, byte high) {
        return (low & 255) + ((high & 255) * 256);
    }

    public void setDistance(long distance) {
        if (mStartTotalDistance == 0 && mTotalDistance != 0)
            mStartTotalDistance = mTotalDistance;

        mDistance = distance;
    }

    public void setCurrentTime(int currentTime) {
        if (mRideTime > (currentTime + TIME_BUFFER))
            mLastRideTime += mRideTime;
        mRideTime = currentTime;
    }

    public void setTopSpeed(int topSpeed) {
        if (topSpeed > mTopSpeed)
            mTopSpeed = topSpeed;
    }

    public void setVoltageSag(int voltSag) {
        if ((voltSag < mVoltageSag) && (voltSag > 0))
            mVoltageSag = voltSag;
    }

    public void setBatteryPercent(int battery) {
        mBattery = battery;
        mAverageBattery = battery;
    }

    private void startSpeedAlarmCount() {
        mSpeedAlarmExecuting = true;
        TimerTask stopSpeedAlarmExecuring = new TimerTask() {
            @Override
            public void run() {
                mSpeedAlarmExecuting = false;
                Timber.i("Stop Speed <<<<<<<<<");
            }
        };
        Timer timerCurrent = new Timer();
        timerCurrent.schedule(stopSpeedAlarmExecuring, 170);

    }

    private void startTempAlarmCount() {
        mTemperatureAlarmExecuting = true;
        TimerTask stopTempAlarmExecuting = new TimerTask() {
            @Override
            public void run() {
                mTemperatureAlarmExecuting = false;
                Timber.i("Stop Temp <<<<<<<<<");
            }
        };
        Timer timerTemp = new Timer();
        timerTemp.schedule(stopTempAlarmExecuting, 570);
    }

    private void startCurrentAlarmCount() {
        mCurrentAlarmExecuting = true;
        TimerTask stopCurrentAlarmExecuring = new TimerTask() {
            @Override
            public void run() {
                mCurrentAlarmExecuting = false;
                Timber.i("Stop Curr <<<<<<<<<");
            }

        };
        Timer timerCurrent = new Timer();
        timerCurrent.schedule(stopCurrentAlarmExecuring, 170);
    }

    private void playWarningSpeed(Context mContext) {
        MediaPlayer mp1 = MediaPlayer.create(mContext, R.raw.sound_warning_speed);
        mp1.start();
        mp1.setOnCompletionListener(mp11 -> mp11.release());
    }

    private void checkAlarmStatus(Context mContext) {
        // SPEED ALARM
        if (!mSpeedAlarmExecuting) {
            if (mAlteredAlarms) {
                double resultFactor = ((float) mSpeed / 100.0) / ((mRotationSpeed / mRotationVoltage) * ((float) mVoltage / 100.0) * mPowerFactor);

                if (resultFactor > mAlarmFactor3) {
                    startSpeedAlarmCount();
                    raiseAlarm(ALARM_TYPE.SPEED3, mContext);
                } else if (resultFactor > mAlarmFactor2) {
                    startSpeedAlarmCount();
                    raiseAlarm(ALARM_TYPE.SPEED2, mContext);
                } else if (resultFactor > mAlarmFactor1) {
                    startSpeedAlarmCount();
                    raiseAlarm(ALARM_TYPE.SPEED1, mContext);
                } else if (mAdvanceWarningSpeed != 0 && getSpeedDouble() >= mAdvanceWarningSpeed) {
                    playWarningSpeed(mContext);
                }
            } else {
                if (mAlarm1Speed > 0 && mAlarm1Battery > 0 &&
                        mAverageBattery <= mAlarm1Battery && mSpeed >= mAlarm1Speed) {
                    startSpeedAlarmCount();
                    raiseAlarm(ALARM_TYPE.SPEED1, mContext);
                } else if (mAlarm2Speed > 0 && mAlarm2Battery > 0 &&
                        mAverageBattery <= mAlarm2Battery && mSpeed >= mAlarm2Speed) {
                    startSpeedAlarmCount();
                    raiseAlarm(ALARM_TYPE.SPEED2, mContext);
                } else if (mAlarm3Speed > 0 && mAlarm3Battery > 0 &&
                        mAverageBattery <= mAlarm3Battery && mSpeed >= mAlarm3Speed) {
                    startSpeedAlarmCount();
                    raiseAlarm(ALARM_TYPE.SPEED3, mContext);
                }
            }
        }

        if (mAlarmCurrent > 0 &&
                mCurrent >= mAlarmCurrent && !mCurrentAlarmExecuting) {
            startCurrentAlarmCount();
            raiseAlarm(ALARM_TYPE.CURRENT, mContext);

        }
        if (mAlarmTemperature > 0 && mTemperature >= mAlarmTemperature && !mTemperatureAlarmExecuting) {
            startTempAlarmCount();
            raiseAlarm(ALARM_TYPE.TEMPERATURE, mContext);

        }
    }

    private void raiseAlarm(ALARM_TYPE alarmType, Context mContext) {
        Vibrator v = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0};
        Intent intent = new Intent(Constants.ACTION_ALARM_TRIGGERED);
        intent.putExtra(Constants.INTENT_EXTRA_ALARM_TYPE, alarmType);

        switch (alarmType) {
            case SPEED1:
                pattern = new long[]{0, 100, 100};
//                mSpeedAlarmExecuted = true;
                break;
            case SPEED2:
                pattern = new long[]{0, 100, 100};
//                mSpeedAlarmExecuted = true;
                break;
            case SPEED3:
                pattern = new long[]{0, 100, 100};
//                mSpeedAlarmExecuted = true;
                break;

            case CURRENT:
                pattern = new long[]{0, 50, 50, 50, 50};
//                mCurrentAlarmExecuted = true;
                break;
            case TEMPERATURE:
                pattern = new long[]{0, 500, 500};
//                mCurrentAlarmExecuted = true;
                break;
        }
        mContext.sendBroadcast(intent);
        if (v.hasVibrator() && !mDisablePhoneVibrate)
            v.vibrate(pattern, -1);
        if (!mDisablePhoneBeep) {
            playBeep(alarmType);
        }
    }

    public void updateGraph(Context mContext) {

		Intent intent = new Intent(Constants.ACTION_WHEEL_DATA_AVAILABLE);

		if (mNewWheelSettings) {
			intent.putExtra(Constants.INTENT_EXTRA_WHEEL_SETTINGS, true);
			mNewWheelSettings = false;
		}

        if (graph_last_update_time + GRAPH_UPDATE_INTERVAL < Calendar.getInstance().getTimeInMillis()) {
            graph_last_update_time = Calendar.getInstance().getTimeInMillis();
            intent.putExtra(Constants.INTENT_EXTRA_GRAPH_UPDATE_AVILABLE, true);
            currentAxis.add((float) getCurrentDouble());
            speedAxis.add((float) getSpeedDouble());
            xAxis.add(new SimpleDateFormat("HH:mm:ss", Locale.US).format(Calendar.getInstance().getTime()));
            if (speedAxis.size() > (3600000 / GRAPH_UPDATE_INTERVAL)) {
                speedAxis.remove(0);
                currentAxis.remove(0);
                xAxis.remove(0);
            }
        }

		if (mAlarmsEnabled)
			checkAlarmStatus(mContext);
		timestamp_last = timestamp_raw;
		mContext.sendBroadcast(intent);
    }

    void full_reset() {
        if (mWheelType == WHEEL_TYPE.INMOTION) InMotionAdapter.stopTimer();
        if (mWheelType == WHEEL_TYPE.NINEBOT_Z) NinebotZAdapter.stopTimer();
        if (mWheelType == WHEEL_TYPE.NINEBOT) NinebotAdapter.stopTimer();
        mBluetoothLeService = null;
        mWheelType = WHEEL_TYPE.Unknown;
        xAxis.clear();
        speedAxis.clear();
        currentAxis.clear();
        reset();
        resetBmsData();
    }

    void reset() {
        mSpeed = 0;
        mTotalDistance = 0;
        mCurrent = 0;
        mTemperature = 0;
		mTemperature2 = 0;
		mAngle = 0;
		mRoll = 0;
        mMode = 0;
        mBattery = 0;
        //mAverageBatteryCount = 0;
        mAverageBattery = 0;
        mVoltage = 0;
        mVoltageSag = 20000;
        mRideTime = 0;
		mRidingTime = 0;
        mTopSpeed = 0;
        mFanStatus = 0;
		mDistance = 0;
		mUserDistance = 0;
        mName = "";
        mModel = "";
		mModeStr = "";
        mVersion = "";
        mSerialNumber = "";
        mBtName = "";
        rideStartTime = 0;
        mStartTotalDistance = 0;
		mWheelTiltHorizon = 0;
		mWheelLightEnabled = false;
		mWheelLedEnabled = false;
		mWheelButtonDisabled = false;
		mWheelMaxSpeed = 0;
		mWheelSpeakerVolume = 50;

		protoVer = "";
	
    }

    boolean detectWheel(BluetoothLeService bluetoothService, String deviceAddress) {
        //audioTrack.write(buffer, 20000, buffer.length);

        mBluetoothLeService = bluetoothService;
        Context mContext = bluetoothService.getApplicationContext();
        String advData = SettingsUtil.getAdvDataForWheel(mContext,deviceAddress);
         //String wheel_Type = "";
        protoVer = "";
        if (advData.compareTo("4e421300000000ec")==0) {
            protoVer = "S2";
        } else if ((advData.compareTo("4e421400000000eb")==0) || (advData.compareTo("4e422000000000df")==0) ||
                (advData.compareTo("4e422200000000dd")==0) || (advData.compareTo("4e4230cf")==0)
                || advData.startsWith("5600")) {
            protoVer = "Mini";
        }

        Class<R.array> res = R.array.class;
        String wheel_types[] = mContext.getResources().getStringArray(R.array.wheel_types);
        for (String wheel_Type : wheel_types) {
            boolean detected_wheel = true;
            Field services_res = null;
            try {
                services_res = res.getField(wheel_Type + "_services");
            } catch (Exception ignored) {
            }
            int services_res_id = 0;
            if (services_res != null)
                try {
                    services_res_id = services_res.getInt(null);
                } catch (Exception ignored) {
                }

            String services[] = mContext.getResources().getStringArray(services_res_id);

            if (services.length != mBluetoothLeService.getSupportedGattServices().size())
                continue;

            for (String service_uuid : services) {
                UUID s_uuid = UUID.fromString(service_uuid.replace("_", "-"));
                BluetoothGattService service = mBluetoothLeService.getGattService(s_uuid);
                if (service != null) {
                    Field characteristic_res = null;
                    try {
                        characteristic_res = res.getField(wheel_Type + "_" + service_uuid);
                    } catch (Exception ignored) {
                    }
                    int characteristic_res_id = 0;
                    if (characteristic_res != null)
                        try {
                            characteristic_res_id = characteristic_res.getInt(null);
                        } catch (Exception ignored) {
                        }
                    String characteristics[] = mContext.getResources().getStringArray(characteristic_res_id);
                    for (String characteristic_uuid : characteristics) {
                        UUID c_uuid = UUID.fromString(characteristic_uuid.replace("_", "-"));
                        BluetoothGattCharacteristic characteristic = service.getCharacteristic(c_uuid);
                        if (characteristic == null) {
                            detected_wheel = false;
                            break;
                        }
                    }
                } else {
                    detected_wheel = false;
                    break;
                }
            }

            if (detected_wheel) {
                final Intent intent = new Intent(Constants.ACTION_WHEEL_TYPE_RECOGNIZED); // update preferences
                intent.putExtra(Constants.INTENT_EXTRA_WHEEL_TYPE, wheel_Type);
                mContext.sendBroadcast(intent);
                Timber.i("Protocol recognized as %s", wheel_Type);
                //System.out.println("WheelRecognizedWD");
                if (mContext.getResources().getString(R.string.gotway).equals(wheel_Type) && (mBtName.equals("RW") || mName.startsWith("ROCKW"))) {
                    Timber.i("It seems to be RochWheel, force to Kingsong proto");
                    wheel_Type = mContext.getResources().getString(R.string.kingsong);
                }
                if (mContext.getResources().getString(R.string.kingsong).equals(wheel_Type)) {
                    mWheelType = WHEEL_TYPE.KINGSONG;
                    BluetoothGattService targetService = mBluetoothLeService.getGattService(UUID.fromString(Constants.KINGSONG_SERVICE_UUID));
                    BluetoothGattCharacteristic notifyCharacteristic = targetService.getCharacteristic(UUID.fromString(Constants.KINGSONG_READ_CHARACTER_UUID));
                    mBluetoothLeService.setCharacteristicNotification(notifyCharacteristic, true);
                    BluetoothGattDescriptor descriptor = notifyCharacteristic.getDescriptor(UUID.fromString(Constants.KINGSONG_DESCRIPTER_UUID));
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    mBluetoothLeService.writeBluetoothGattDescriptor(descriptor);

                    return true;
                } else if (mContext.getResources().getString(R.string.gotway).equals(wheel_Type)) {
                    mWheelType = WHEEL_TYPE.GOTWAY;
                    BluetoothGattService targetService = mBluetoothLeService.getGattService(UUID.fromString(Constants.GOTWAY_SERVICE_UUID));
                    BluetoothGattCharacteristic notifyCharacteristic = targetService.getCharacteristic(UUID.fromString(Constants.GOTWAY_READ_CHARACTER_UUID));
                    mBluetoothLeService.setCharacteristicNotification(notifyCharacteristic, true);
                    // Let the user know it's working by making the wheel beep
                    mBluetoothLeService.writeBluetoothGattCharacteristic("b".getBytes());
                    return true;
                } else if (mContext.getResources().getString(R.string.inmotion).equals(wheel_Type)) {
                    mWheelType = WHEEL_TYPE.INMOTION;
                    BluetoothGattService targetService = mBluetoothLeService.getGattService(UUID.fromString(Constants.INMOTION_SERVICE_UUID));
                    BluetoothGattCharacteristic notifyCharacteristic = targetService.getCharacteristic(UUID.fromString(Constants.INMOTION_READ_CHARACTER_UUID));
                    mBluetoothLeService.setCharacteristicNotification(notifyCharacteristic, true);
                    BluetoothGattDescriptor descriptor = notifyCharacteristic.getDescriptor(UUID.fromString(Constants.INMOTION_DESCRIPTER_UUID));
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    mBluetoothLeService.writeBluetoothGattDescriptor(descriptor);
                    if (SettingsUtil.hasPasswordForWheel(mContext, mBluetoothLeService.getBluetoothDeviceAddress())) {
                        String inmotionPassword = SettingsUtil.getPasswordForWheel(mBluetoothLeService.getApplicationContext(), mBluetoothLeService.getBluetoothDeviceAddress());
                        InMotionAdapter.getInstance().startKeepAliveTimer(mBluetoothLeService, inmotionPassword);
                        return true;
                    }
                    return false;
                } else if (mContext.getResources().getString(R.string.ninebot_z).equals(wheel_Type)) {
                    Timber.i("Trying to start Ninebot Z");
                    mWheelType = WHEEL_TYPE.NINEBOT_Z;
                    BluetoothGattService targetService = mBluetoothLeService.getGattService(UUID.fromString(Constants.NINEBOT_Z_SERVICE_UUID));
                    Timber.i("service UUID");
                    BluetoothGattCharacteristic notifyCharacteristic = targetService.getCharacteristic(UUID.fromString(Constants.NINEBOT_Z_READ_CHARACTER_UUID));
                    Timber.i("read UUID");
                    if (notifyCharacteristic == null) {
                        Timber.i("it seems that RX UUID doesn't exist");
                    }
                    mBluetoothLeService.setCharacteristicNotification(notifyCharacteristic, true);
                    Timber.i("notify UUID");
                    BluetoothGattDescriptor descriptor = notifyCharacteristic.getDescriptor(UUID.fromString(Constants.NINEBOT_Z_DESCRIPTER_UUID));
                    Timber.i("descr UUID");
                    if (descriptor == null) {
                        Timber.i("it seems that descr UUID doesn't exist");
                    }
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    Timber.i("enable notify UUID");
                    mBluetoothLeService.writeBluetoothGattDescriptor(descriptor);
                    Timber.i("write notify");
                    NinebotZAdapter.getInstance().startKeepAliveTimer(mBluetoothLeService, "");
                    Timber.i("starting ninebot adapter");
                    return true;
                } else if (mContext.getResources().getString(R.string.ninebot).equals(wheel_Type)) {
                    Timber.i("Trying to start Ninebot");
                    advData = SettingsUtil.getAdvDataForWheel(mContext, deviceAddress);
                    mWheelType = WHEEL_TYPE.NINEBOT;
                    BluetoothGattService targetService = mBluetoothLeService.getGattService(UUID.fromString(Constants.NINEBOT_SERVICE_UUID));
                    Timber.i("service UUID");
                    BluetoothGattCharacteristic notifyCharacteristic = targetService.getCharacteristic(UUID.fromString(Constants.NINEBOT_READ_CHARACTER_UUID));
                    Timber.i("read UUID");
                    if (notifyCharacteristic == null) {
                        Timber.i("it seems that RX UUID doesn't exist");
                    }
                    mBluetoothLeService.setCharacteristicNotification(notifyCharacteristic, true);
                    Timber.i("notify UUID");
                    BluetoothGattDescriptor descriptor = notifyCharacteristic.getDescriptor(UUID.fromString(Constants.NINEBOT_DESCRIPTER_UUID));
                    Timber.i("descr UUID");
                    if (descriptor == null) {
                        Timber.i("it seems that descr UUID doesn't exist");
                    }
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    Timber.i("enable notify UUID");
                    mBluetoothLeService.writeBluetoothGattDescriptor(descriptor);
                    Timber.i("write notify");

                    String protoVer = "";
                    if (advData.compareTo("4e421300000000ec") == 0) {
                        protoVer = "S2";
                    } else if ((advData.compareTo("4e421400000000eb") == 0) || (advData.compareTo("4e422000000000df") == 0) ||
                            (advData.compareTo("4e422200000000dd") == 0) || (advData.compareTo("4e4230cf") == 0)
                            || advData.startsWith("5600")) {
                        protoVer = "Mini";
                    }
                    NinebotAdapter.getInstance().setProtoVer(protoVer);
                    NinebotAdapter.getInstance().startKeepAliveTimer(mBluetoothLeService, "");
                    Timber.i("starting ninebot adapter");
                    return true;
                }
            }
            else {
                Timber.i("Protocol recognized as Unknown");
            }
        }
        return false;
    }

    public void setSpeed(int i) {
        mSpeed = i;
    }

    public void setWheelLight(boolean lightState) {
        mWheelLightEnabled = lightState;
    }

    public void setLed(boolean ledState) {
        mWheelLedEnabled = ledState;
    }

    public void setButtonDisabled(boolean handleButtonState) {
        mWheelButtonDisabled = handleButtonState;
    }

    public void setMaxSpeed(int maxSpeed) {
        mWheelMaxSpeed = maxSpeed;
    }

    public void setSpeakerVolume(int speakerVolumeState) {
        mWheelSpeakerVolume = speakerVolumeState;
    }

    public void setTiltHorizon(int tiltHorizon) {
        mWheelTiltHorizon = tiltHorizon;
    }

    public void setSerialNumber(String serialNumber) {
        mSerialNumber = serialNumber;
    }

    public void setModel(String modelString) {
        mModel = modelString;
    }

    public void setVersion(String version) {
        mVersion = version;
    }

    public void setNewSettings(boolean b) {
        mNewWheelSettings = b;
    }

    public void addAlert(String alertText) {
        if (mAlert == "") {
            mAlert = alertText;
        } else {
            mAlert = mAlert + " | " + alertText;
        }
    }

    public void setVoltage(int i) {
        mVoltage = i;
    }

    public void setCurrent(int i) {
        mCurrent = i;
    }

    public void setTemperature(int temperature) {
        mTemperature = temperature;
    }

    public void setTemperature2(int temperature2) {
        mTemperature2 = temperature2;
    }

    public void setTotalDistance(long l) {
        mTotalDistance = l;
    }

    public void scaleTotalDistanceTo(double scale) {
        mTotalDistance = Math.round(mTotalDistance * scale);
    }

    public void setAngle(double angle) {
        mAngle = angle;
    }

    public void setRoll(double roll) {
        mRoll = roll;
    }

    public void setModeStr(String workModeString) {
        mModeStr = workModeString;
    }

    public long getRideStartTime() {
        return rideStartTime;
    }

    public boolean getUseRatio() {
        return mUseRatio;
    }

    public boolean getBetterPercents() {
        return mBetterPercents;
    }

    public void setRideStartTime(long timeInMillis) {
        rideStartTime = timeInMillis;
    }

    public void setRidingTime(int i) {
        mRidingTime = i;
    }

    public int getVoltage() {
        return mVoltage;
    }

    public void setMode(byte datum) {
        mMode = datum;
    }

    public void setFanStatus(byte fanstatus) {
        mFanStatus = fanstatus;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setWheelMaxSpeed(int maxSpeed) {
        mWheelMaxSpeed = maxSpeed;
    }
}
