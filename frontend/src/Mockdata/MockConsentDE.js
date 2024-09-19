export default {
  id: '09bc2173-0934-4fae-9026-76bf8b770b29',
  name: 'Mein Testfeld',
  clientName: 'Basic FMIS',
  userName: 'Bob',
  startTime: '2021-05-01',
  endTime: '2023-10-01',
  permissionType: 'Write',
  permissions: [
    {
      name: 'Feldgrenzen',
      read: true,
      write: true,
      update: true,
    },
    {
      name: 'Arbeitsprotokoll',
      read: true,
      write: true,
      update: false,
    },
    {
      name: 'Arbeitsauftrag',
      read: true,
      write: false,
      update: false,
    },
    {
      name: 'Applikationen',
      read: true,
      write: false,
      update: false,
    },
    {
      name: 'Applikationenempfehlungen',
      read: true,
      write: false,
      update: false,
    },
    {
      name: 'Dateien',
      read: true,
      write: true,
      update: true,
    }
  ]
};
