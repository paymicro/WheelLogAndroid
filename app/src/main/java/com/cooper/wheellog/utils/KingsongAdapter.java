package com.cooper.wheellog.utils;

import com.cooper.wheellog.WheelData;

import java.util.Calendar;
import java.util.Locale;

import timber.log.Timber;

public class KingsongAdapter implements IWheelAdapter {
    private static KingsongAdapter mInstance;
    private static final double KS18L_SCALER = 0.83;
    private boolean m18Lkm = true;
    private int mKSAlarm1Speed = 0;
    private int mKSAlarm2Speed = 0;
    private int mKSAlarm3Speed = 0;
    private boolean mKSAlertsAndSpeedupdated = false;

    @Override
    public boolean decode(byte[] data) {
        WheelData wd = WheelData.getInstance();
        if (data.length >= 20) {
            int a1 = data[0] & 255;
            int a2 = data[1] & 255;
            if (a1 != 170 || a2 != 85) {
                return false;
            }
            if ((data[16] & 255) == 169) { // Live data
                wd.setVoltage(MathsUtil.byteArrayInt2(data[2], data[3]));
                wd.setSpeed(MathsUtil.byteArrayInt2(data[4], data[5]));
                wd.setTotalDistance(MathsUtil.byteArrayInt4(data[6], data[7], data[8], data[9]));
                set18Lkm(m18Lkm);
                wd.setCurrent(((data[10]&0xFF) + (data[11]<<8)));

                wd.setTemperature(MathsUtil.byteArrayInt2(data[12], data[13]));
                if ((data[15] & 255) == 224) {
                    wd.setMode(data[14]);
                    wd.setModeStr(String.format(Locale.US, "%d", data[14]));
                }

                int battery;

                if (wd.getModel().matches("KS-18L|KS-16X|RW|KS-18LH|KS-S18|^ROCKW")) {
                    if (wd.getBetterPercents()) {
                        if (wd.getVoltage() > 8350) {
                            battery = 100;
                        } else if (wd.getVoltage() > 6800) {
                            battery = (wd.getVoltage() - 6650) / 17;
                        } else if (wd.getVoltage() > 6400) {
                            battery = (wd.getVoltage() - 6400) / 45;
                        } else {
                            battery = 0;
                        }
                    } else {
                        if (wd.getVoltage() < 6250) {
                            battery = 0;
                        } else if (wd.getVoltage() >= 8250) {
                            battery = 100;
                        } else {
                            battery = (wd.getVoltage() - 6250) / 20;
                        }
                    }
                } else {
                    if (wd.getBetterPercents()) {
                        if (wd.getVoltage() > 6680) {
                            battery = 100;
                        } else if (wd.getVoltage() > 5440) {
                            battery = (int) Math.round((wd.getVoltage() - 5320) / 13.6);
                        } else if (wd.getVoltage() > 5120) {
                            battery = (wd.getVoltage() - 5120) / 36;
                        } else {
                            battery = 0;
                        }
                    } else {
                        if (wd.getVoltage() < 5000) {
                            battery = 0;
                        } else if (wd.getVoltage() >= 6600) {
                            battery = 100;
                        } else {
                            battery = (wd.getVoltage() - 5000) / 16;
                        }
                    }
                }

                wd.setBatteryPercent(battery);

                return true;
            } else if ((data[16] & 255) == 185) { // Distance/Time/Fan Data
                long distance = MathsUtil.byteArrayInt4(data[2], data[3], data[4], data[5]);
                wd.setDistance(distance);
                int currentTime = (int) (Calendar.getInstance().getTimeInMillis() - wd.getRideStartTime()) / 1000;
                wd.setCurrentTime(currentTime);
                wd.setTopSpeed(MathsUtil.byteArrayInt2(data[8], data[9]));
                wd.setFanStatus(data[12]);
            } else if ((data[16] & 255) == 187) { // Name and Type data
                int end = 0;
                int i = 0;
                while (i < 14 && data[i + 2] != 0) {
                    end++;
                    i++;
                }
                String name = new String(data, 2, end).trim();
                String model = "";
                wd.setName(name);
                String[] ss = name.split("-");
                for (i = 0; i < ss.length - 1; i++) {
                    if (i != 0) {
                        model += "-";
                    }
                    model += ss[i];
                }
                wd.setModel(model);
                try {
                    wd.setVersion(String.format(Locale.US, "%.2f", ((double)(Integer.parseInt(ss[ss.length - 1])/100.0))));
                } catch (Exception ignored) {
                }

            } else if ((data[16] & 255) == 179) { // Serial Number
                byte[] sndata = new byte[18];
                System.arraycopy(data, 2, sndata, 0, 14);
                System.arraycopy(data, 17, sndata, 14, 3);
                sndata[17] = (byte) 0;
                wd.setSerialNumber(new String(sndata));
                updateKSAlarmAndSpeed();
            }
            else if ((data[16] & 255) == 164 || (data[16] & 255) == 181) { //0xa4 || 0xb5 max speed and alerts
                wd.setWheelMaxSpeed((data[10] & 255));
                mKSAlarm3Speed = (data[8] & 255);
                mKSAlarm2Speed = (data[6] & 255);
                mKSAlarm1Speed = (data[4] & 255);
                mKSAlertsAndSpeedupdated = true;
                // after received 0xa4 send same repeat data[2] =0x01 data[16] = 0x98
                if((data[16] & 255) == 164)
                {
                    data[16] = (byte)0x98;
                    WheelData.getInstance().getBluetoothLeService().writeBluetoothGattCharacteristic(data);
                }

            }
        }
        return false;
    }

    public static KingsongAdapter getInstance() {
        Timber.i("Get instance");
        if (mInstance == null) {
            Timber.i("New instance");
            mInstance = new KingsongAdapter();
        }
        return mInstance;
    }

    public void set18Lkm(boolean enabled) {
        m18Lkm = enabled;
        if ((WheelData.getInstance().getModel().compareTo("KS-18L") == 0) && !m18Lkm) {
            WheelData.getInstance().scaleTotalDistanceTo(KS18L_SCALER);
        }
    }

    @Override
    public void updatePedalsMode(int pedalsMode) {
        byte[] data = new byte[20];
        data[0] = (byte) 0xAA;
        data[1] = (byte) 0x55;
        data[2] = (byte) pedalsMode;
        data[3] = (byte) 0xE0;
        data[16] = (byte) 0x87;
        data[17] = (byte) 0x15;
        data[18] = (byte) 0x5A;
        data[19] = (byte) 0x5A;
        WheelData.getInstance().getBluetoothLeService().writeBluetoothGattCharacteristic(data);
    }

    @Override
    public void updateLightMode(int lightMode) {
        byte[] data = new byte[20];
        data[0] = (byte) 0xAA;
        data[1] = (byte) 0x55;
        data[2] = (byte) (lightMode + 0x12);
        data[3] = (byte) 0x01;
        data[16] = (byte) 0x73;
        data[17] = (byte) 0x14;
        data[18] = (byte) 0x5A;
        data[19] = (byte) 0x5A;
        WheelData.getInstance().getBluetoothLeService().writeBluetoothGattCharacteristic(data);
    }

    @Override
    public void updateMaxSpeed(int wheelMaxSpeed) {
        if (WheelData.getInstance().getWheelMaxSpeed() != wheelMaxSpeed) {
            WheelData.getInstance().setWheelMaxSpeed(wheelMaxSpeed);
            updateKSAlarmAndSpeed();
        }
    }

    public void updateKSAlarmAndSpeed() {
        byte[] data = new byte[20];
        data[0] = (byte) 0xAA;
        data[1] = (byte) 0x55;
        data[2] = (byte) mKSAlarm1Speed;
        data[4] = (byte) mKSAlarm2Speed;
        data[6] = (byte) mKSAlarm3Speed;
        data[8] = (byte) WheelData.getInstance().getWheelMaxSpeed();
        data[16] = (byte) 0x85;

        if ((WheelData.getInstance().getWheelMaxSpeed() | mKSAlarm3Speed | mKSAlarm2Speed | mKSAlarm1Speed) == 0){
            data[16] = (byte) 0x98; // request speed & alarm values from wheel
        }

        data[17] = (byte) 0x14;
        data[18] = (byte) 0x5A;
        data[19] = (byte) 0x5A;
        WheelData.getInstance().getBluetoothLeService().writeBluetoothGattCharacteristic(data);
    }

    public void updateKSAlarm1(int wheelKSAlarm1) {
        if (mKSAlarm1Speed != wheelKSAlarm1) {
            mKSAlarm1Speed = wheelKSAlarm1;
            updateKSAlarmAndSpeed();
        }
    }

    public void updateKSAlarm2(int wheelKSAlarm2) {
        if (mKSAlarm2Speed != wheelKSAlarm2) {
            mKSAlarm2Speed = wheelKSAlarm2;
            updateKSAlarmAndSpeed();
        }
    }

    public void updateKSAlarm3(int wheelKSAlarm3) {
        if (mKSAlarm3Speed != wheelKSAlarm3) {
            mKSAlarm3Speed = wheelKSAlarm3;
            updateKSAlarmAndSpeed();
        }
    }

    public int getKSAlarm1Speed() {
        return mKSAlarm1Speed;
    }

    public int getKSAlarm2Speed() {
        return mKSAlarm2Speed;
    }

    public int getKSAlarm3Speed() {
        return mKSAlarm3Speed;
    }

    public boolean isPrefReceived(){
        return mKSAlertsAndSpeedupdated;
    }
}
