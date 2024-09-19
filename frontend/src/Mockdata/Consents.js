export default [
  {
    id: '086d6099-f88f-4bf5-aa57-e6094263790d',
    createdAt: '2022-07-08T12:02:02.338Z',
    consentGiverId: '85f0be83-c236-4d5e-a525-6a751bdafc68',
    requestorIdentity: {
      userId: '85f0be83-c236-4d5e-a525-6a751bdafc68',
      clientId: 'swagger',
      userName: 'bill',
      clientName: 'swagger'
    },
    startTime: '2022-04-28T10:11:12Z',
    endTime: '2024-04-28T10:11:12Z',
    state: 'ACTIVE',
    grantFullAccess: false,
    grantAccessToAllTwins: false,
    twinIds: [
      'bergkoppel-1',
      'großemühle-1'
    ],
    grantAllTwinResourcePermissions: false,
    twinResourcePermissions: {
      WORK_RECORDS_FERTILIZATION: ['READ'],
      ARABLE_AREA: [
        'UPDATE',
        'READ'
      ]
    },
    dataUsageStatement: 'We will only use your Work Records and field geometries to provide you our service of analyzing and optimizing your seeding.\nThis is necessary to enable us to analyze your actual seeding process and compute possible alternatives.We will not do anything else with your data.\nIf this is not okay for you: Sorry, you can not use our service',
    additionalNotes: 'as reference see contract in harvesting folder.'
  },
  {
    id: '9e042ed0-aa6d-4a2d-9e5a-af038cfe824f',
    createdAt: '2022-07-08T12:10:00.666Z',
    consentGiverId: '85f0be83-c236-4d5e-a525-6a751bdafc68',
    requestorIdentity: {
      userId: '85f0be83-c236-4d5e-a525-6a751bdafc68',
      clientId: 'swagger',
      userName: 'bill',
      clientName: 'swagger'
    },
    startTime: null,
    endTime: null,
    state: 'REVOKED',
    grantFullAccess: true,
    grantAccessToAllTwins: true,
    twinIds: null,
    grantAllTwinResourcePermissions: true,
    twinResourcePermissions: null,
    dataUsageStatement: 'Basic-FMIS will use your Data to provide you access to your field data',
    additionalNotes: 'as reference see contract in harvesting folder.'
  }
];
