package com.cooper.wheellog.utils;

import android.os.Handler;

import com.cooper.wheellog.WheelData;

import timber.log.Timber;

public class GotwayAdapter implements IWheelAdapter {
    private static GotwayAdapter INSTANCE;
    private static final double RATIO_GW = 0.875;
    private int mVoltageScaler = 0;
    private int mGotwayVoltageScaler = 0;
    private int mGotwayNegative = -1;

    @Override
    public boolean decode(byte[] data) {
        WheelData wd = WheelData.getInstance();

        if (data.length >= 20) {
            int a1 = data[0] & 255;
            int a2 = data[1] & 255;
            int a19 = data[18] & 255;
            if (a1 != 85 || a2 != 170 || a19 != 0) {
                return false;
            }

            int speed = 0;
            if (data[5] >= 0) {
                if (mGotwayNegative == 0)
                    speed = (int) Math.abs(((data[4] * 256.0) + data[5]) * 3.6);
                else
                    speed = ((int) (((data[4] * 256.0) + data[5]) * 3.6)) * mGotwayNegative;
            }
            else {
                if (mGotwayNegative == 0)
                    speed = (int) Math.abs((((data[4] * 256.0) + 256.0) + data[5]) * 3.6);
                else
                    speed = ((int) ((((data[4] * 256.0) + 256.0) + data[5]) * 3.6)) * mGotwayNegative;
            }

            if (wd.getUseRatio()) {
                wd.setSpeed((int) Math.round(speed * RATIO_GW));
            } else {
                wd.setSpeed(speed);
            }

            int temperature = (int) Math.round(((((data[12] * 256) + data[13]) / 340.0) + 35) * 100);
            wd.setTemperature(temperature);
            wd.setTemperature2(temperature);

            long distance = MathsUtil.byteArrayInt2(data[9], data[8]);
            if (wd.getUseRatio()) {
                distance = Math.round(distance * RATIO_GW);
            }
            wd.setDistance(distance);

            int voltage = (data[2] * 256) + (data[3] & 255);
            wd.setVoltage(voltage);

            wd.setCurrent(-((data[10] * 256) + data[11]));

            int battery;
            if (wd.getBetterPercents()) {
                if (voltage > 6680) {
                    battery = 100;
                } else if (voltage > 5440) {
                    battery = (voltage - 5380) / 13;
                } else if (voltage > 5290) {
                    battery = (int) Math.round((voltage - 5290) / 32.5);
                } else {
                    battery = 0;
                }
            } else {
                if (voltage <= 5290) {
                    battery = 0;
                } else if (voltage >= 6580) {
                    battery = 100;
                } else {
                    battery = (voltage - 5290) / 13;
                }
            }
            wd.setBatteryPercent(battery);
            voltage = (int)Math.round(voltage*(1+(0.25*mVoltageScaler)));
            wd.setVoltage(voltage);

            return true;
        } else if (data.length >= 10) {
            int a1 = data[0];
            int a5 = data[4] & 255;
            int a6 = data[5] & 255;
            if (a1 != 90 || a5 != 85 || a6 != 170) {
                return false;
            }

            long totalDistance =  ((data[6]&0xFF) <<24) + ((data[7]&0xFF) << 16) + ((data[8] & 0xFF) <<8) + (data[9] & 0xFF);
            if (wd.getUseRatio()) {
                wd.setTotalDistance(Math.round(totalDistance * RATIO_GW));
            } else {
                wd.setTotalDistance(totalDistance);
            }
        }
        return false;
    }

    @Override
    public void updatePedalsMode(int pedalsMode) {
        switch (pedalsMode) {
            case 0:
                WheelData.getInstance().getBluetoothLeService().writeBluetoothGattCharacteristic("h".getBytes());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        WheelData.getInstance().getBluetoothLeService().writeBluetoothGattCharacteristic("b".getBytes());
                    }
                }, 100);
                break;
            case 1:
                WheelData.getInstance().getBluetoothLeService().writeBluetoothGattCharacteristic("f".getBytes());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        WheelData.getInstance().getBluetoothLeService().writeBluetoothGattCharacteristic("b".getBytes());
                    }
                }, 100);
                break;
            case 2:
                WheelData.getInstance().getBluetoothLeService().writeBluetoothGattCharacteristic("s".getBytes());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        WheelData.getInstance().getBluetoothLeService().writeBluetoothGattCharacteristic("b".getBytes());
                    }
                }, 100);
                break;
        }
    }

    @Override
    public void updateLightMode(int lightMode) {
        switch (lightMode) {
            case 0:
                WheelData.getInstance().getBluetoothLeService().writeBluetoothGattCharacteristic("E".getBytes());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        WheelData.getInstance().getBluetoothLeService().writeBluetoothGattCharacteristic("b".getBytes());
                    }
                }, 100);
                break;
            case 1:
                WheelData.getInstance().getBluetoothLeService().writeBluetoothGattCharacteristic("Q".getBytes());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        WheelData.getInstance().getBluetoothLeService().writeBluetoothGattCharacteristic("b".getBytes());
                    }
                }, 100);
                break;
            case 2:
                WheelData.getInstance().getBluetoothLeService().writeBluetoothGattCharacteristic("T".getBytes());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        WheelData.getInstance().getBluetoothLeService().writeBluetoothGattCharacteristic("b".getBytes());
                    }
                }, 100);
                break;
        }
    }

    @Override
    public void updateMaxSpeed(int wheelMaxSpeed) {
        final byte[] hhh = new byte[1];
        final byte[] lll = new byte[1];
        if (wheelMaxSpeed != 0) {
            int wheelMaxSpeed2 = wheelMaxSpeed;
            //if (mUseRatio) wheelMaxSpeed2 = (int)Math.round(wheelMaxSpeed2/RATIO_GW);
            hhh[0] = (byte)((wheelMaxSpeed2/10)+0x30);
            lll[0] = (byte)((wheelMaxSpeed2%10)+0x30);
            WheelData.getInstance().getBluetoothLeService().writeBluetoothGattCharacteristic("b".getBytes());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    WheelData.getInstance().getBluetoothLeService().writeBluetoothGattCharacteristic("W".getBytes());
                }
            }, 100);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    WheelData.getInstance().getBluetoothLeService().writeBluetoothGattCharacteristic("Y".getBytes());
                }
            }, 200);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    WheelData.getInstance().getBluetoothLeService().writeBluetoothGattCharacteristic(hhh);
                }
            }, 300);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    WheelData.getInstance().getBluetoothLeService().writeBluetoothGattCharacteristic(lll);
                }
            }, 400);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    WheelData.getInstance().getBluetoothLeService().writeBluetoothGattCharacteristic("b".getBytes());
                }
            }, 500);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    WheelData.getInstance().getBluetoothLeService().writeBluetoothGattCharacteristic("b".getBytes());
                }
            }, 600);

        } else {
            WheelData.getInstance().getBluetoothLeService().writeBluetoothGattCharacteristic("b".getBytes());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    WheelData.getInstance().getBluetoothLeService().writeBluetoothGattCharacteristic("\"".getBytes()); // "
                }
            }, 100);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    WheelData.getInstance().getBluetoothLeService().writeBluetoothGattCharacteristic("b".getBytes());
                }
            }, 200);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    WheelData.getInstance().getBluetoothLeService().writeBluetoothGattCharacteristic("b".getBytes());
                }
            }, 300);

        }
    }

    public void setVoltage(int voltage) {
        mVoltageScaler = voltage;
    }

    public static GotwayAdapter getInstance() {
        Timber.i("Get instance");
        if (INSTANCE == null) {
            Timber.i("New instance");
            INSTANCE = new GotwayAdapter();
        }
        return INSTANCE;
    }

    public void setNegative(int negative) {
        mGotwayNegative = negative;
    }
}
