export default [
  {
    createdAt: '2022-04-28T10:11:12.00Z',
    dataUsageStatement: 'We will only use your Work Records and field geometries to provide you our service of analyzing and optimizing your seeding.\nThis is necessary to enable us to analyze your actual seeding process and compute possible alternatives.We will not do anything else with your data.\nIf this is not okay for you: Sorry, you can not use our service}',
    endTime: '2024-04-28T10:11:12.00Z',
    id: 'string',
    receiverId: 'Bill-the-farmer-ID',
    requestAllTwinResourcePermissions: false,
    requestFullAccess: false,
    requestorIdentity: {
      clientId: 'ClientIdOfBasicFMIS-1',
      clientName: 'basic-fmis',
      userId: 'BobsId-1',
      userName: 'bob'
    },
    startTime: '2022-04-28T10:11:12.00Z',
    twinResourcePermissions: {
      GEOMETRIES: [
        'READ',
        'UPDATE'
      ],
      WORK_RECORDS_FERTILIZATION: ['READ']
    }
  },
  {
    createdAt: '2022-05-30T10:11:12.00Z',
    dataUsageStatement: 'We will only use your Work Records and field geometries to provide you our service of analyzing and optimizing your seeding.\nThis is necessary to enable us to analyze your actual seeding process and compute possible alternatives.We will not do anything else with your data.\nIf this is not okay for you: Sorry, you can not use our service}',
    endTime: '2024-08-10T10:11:12.00Z',
    id: 'string',
    receiverId: 'Bill-the-farmer-ID',
    requestAllTwinResourcePermissions: false,
    requestFullAccess: true,
    requestorIdentity: {
      clientId: 'ClientIdOfBasicFMIS-1',
      clientName: 'basic-fmis',
      userId: 'BobsId-1',
      userName: 'bob'
    },
    startTime: '2022-05-30T10:11:12.00Z',
    twinResourcePermissions: null
  }
];
