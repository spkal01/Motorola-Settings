package com.motorola.multivolume;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IMultiVolumeService extends IInterface {
    void changeAppRow(String str, int i, int i2, double d, int i3) throws RemoteException;

    void changeMusicRow(int i, double d) throws RemoteException;

    void registerVolumeController(IMultiVolumeController iMultiVolumeController) throws RemoteException;

    void unregisterVolumeController(IMultiVolumeController iMultiVolumeController) throws RemoteException;

    public static abstract class Stub extends Binder implements IMultiVolumeService {
        public static IMultiVolumeService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.motorola.multivolume.IMultiVolumeService");
            if (queryLocalInterface == null || !(queryLocalInterface instanceof IMultiVolumeService)) {
                return new Proxy(iBinder);
            }
            return (IMultiVolumeService) queryLocalInterface;
        }

        private static class Proxy implements IMultiVolumeService {
            public static IMultiVolumeService sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public void registerVolumeController(IMultiVolumeController iMultiVolumeController) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.motorola.multivolume.IMultiVolumeService");
                    obtain.writeStrongBinder(iMultiVolumeController != null ? iMultiVolumeController.asBinder() : null);
                    if (this.mRemote.transact(1, obtain, (Parcel) null, 1) || Stub.getDefaultImpl() == null) {
                        obtain.recycle();
                    } else {
                        Stub.getDefaultImpl().registerVolumeController(iMultiVolumeController);
                    }
                } finally {
                    obtain.recycle();
                }
            }

            public void unregisterVolumeController(IMultiVolumeController iMultiVolumeController) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.motorola.multivolume.IMultiVolumeService");
                    obtain.writeStrongBinder(iMultiVolumeController != null ? iMultiVolumeController.asBinder() : null);
                    if (this.mRemote.transact(2, obtain, (Parcel) null, 1) || Stub.getDefaultImpl() == null) {
                        obtain.recycle();
                    } else {
                        Stub.getDefaultImpl().unregisterVolumeController(iMultiVolumeController);
                    }
                } finally {
                    obtain.recycle();
                }
            }

            public void changeMusicRow(int i, double d) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.motorola.multivolume.IMultiVolumeService");
                    obtain.writeInt(i);
                    obtain.writeDouble(d);
                    if (this.mRemote.transact(4, obtain, (Parcel) null, 1) || Stub.getDefaultImpl() == null) {
                        obtain.recycle();
                    } else {
                        Stub.getDefaultImpl().changeMusicRow(i, d);
                    }
                } finally {
                    obtain.recycle();
                }
            }

            public void changeAppRow(String str, int i, int i2, double d, int i3) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.motorola.multivolume.IMultiVolumeService");
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeDouble(d);
                    obtain.writeInt(i3);
                    if (this.mRemote.transact(5, obtain, (Parcel) null, 1) || Stub.getDefaultImpl() == null) {
                        obtain.recycle();
                    } else {
                        Stub.getDefaultImpl().changeAppRow(str, i, i2, d, i3);
                    }
                } finally {
                    obtain.recycle();
                }
            }
        }

        public static IMultiVolumeService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
