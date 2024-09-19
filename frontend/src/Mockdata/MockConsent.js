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
      name: 'Geometries',
      read: true,
      write: true,
      update: true,
    },
    {
      name: 'Work Records',
      read: true,
      write: true,
      update: false,
    },
    {
      name: 'Work Orders',
      read: true,
      write: false,
      update: false,
    },
    {
      name: 'Applications',
      read: true,
      write: false,
      update: false,
    },
    {
      name: 'Applications Recommendations',
      read: true,
      write: false,
      update: false,
    },
    {
      name: 'Data Files',
      read: true,
      write: true,
      update: true,
    }
  ]
};
