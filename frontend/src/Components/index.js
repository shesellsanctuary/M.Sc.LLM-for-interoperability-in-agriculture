import Loading from './Loading';
import RouteButton from './RouteButton';
import Title from './Title';
import UserDropdownWrapper from './UserDropdown';
import UserInfo from './UserInfo';
import DeleteFieldButtonWrapper from './DeleteFieldButton';
import ConfirmationModal from './ConfirmationModal';
import CreateFieldButtonWrapper from './CreateFieldButton';
import HeaderWrapper from './Header';
import GenericTable from './GenericTable';
import FieldAdminTableRowWrapper from './FieldAdminTableRow';
import ConsentsTableRowWrapper from './ConsentsTableRow';
import TwinResourcePermissionsTableRow from './TwinResourcePermissionsTableRow';
import RevokeConsentButtonWrapper from './RevokeConsentButton';
import ConsentRequestsTableRowWrapper from './ConsentRequestsTableRow';
import AccessLogsTableRowWrapper from './AccessLogsTableRow';
import Pagination from './Pagination';

const Components = ({ handlers }) => {
  const { authService, adsPlatformApiHandler, formatUtils } = handlers;
  const UserDropdown = UserDropdownWrapper({ authService });
  const DeleteFieldButton = DeleteFieldButtonWrapper({
    adsPlatformApiHandler, ConfirmationModal
  });
  const CreateFieldButton = CreateFieldButtonWrapper({
    adsPlatformApiHandler, ConfirmationModal
  });
  const Header = HeaderWrapper({ Title, UserDropdown });
  const FieldAdminTableRow = FieldAdminTableRowWrapper({ DeleteFieldButton });
  const ConsentsTableRow = ConsentsTableRowWrapper({ formatUtils, RouteButton });
  const RevokeConsentButton = RevokeConsentButtonWrapper({
    adsPlatformApiHandler,
    ConfirmationModal
  });
  const ConsentRequestsTableRow = ConsentRequestsTableRowWrapper({ formatUtils, RouteButton });
  const AccessLogsTableRow = AccessLogsTableRowWrapper({ formatUtils, RouteButton });

  return {
    Loading,
    RouteButton,
    Title,
    UserDropdown,
    UserInfo,
    DeleteFieldButton,
    ConfirmationModal,
    CreateFieldButton,
    Header,
    GenericTable,
    FieldAdminTableRow,
    ConsentsTableRow,
    TwinResourcePermissionsTableRow,
    RevokeConsentButton,
    ConsentRequestsTableRow,
    AccessLogsTableRow,
    Pagination
  };
};

export default Components;
