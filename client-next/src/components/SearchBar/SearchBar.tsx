import { Button, Stack } from 'react-bootstrap';
import { TripQueryVariables } from '../../gql/graphql.ts';
import { LocationInputField } from './LocationInputField.tsx';
import { DepartureArrivalSelect } from './DepartureArrivalSelect.tsx';
import { TimeInputField } from './TimeInputField.tsx';
import { DateInputField } from './DateInputField.tsx';
import { SearchWindowInput } from './SearchWindowInput.tsx';
import { AccessSelect } from './AccessSelect.tsx';
import { EgressSelect } from './EgressSelect.tsx';
import { DirectModeSelect } from './DirectModeSelect.tsx';
import { TransitModeSelect } from './TransitModeSelect.tsx';
import { NumTripPatternsInput } from './NumTripPatternsInput.tsx';
import { ItineraryFilterDebugSelect } from './ItineraryFilterDebugSelect.tsx';
import Navbar from 'react-bootstrap/Navbar';

export function SearchBar({
  onRoute,
  tripQueryVariables,
  setTripQueryVariables,
}: {
  onRoute: () => void;
  tripQueryVariables: TripQueryVariables;
  setTripQueryVariables: (tripQueryVariables: TripQueryVariables) => void;
}) {
  return (
    <div className="search-bar top-content">
      <Navbar.Brand>
        <img
          alt=""
          src="/debug-client-preview/img/otp-logo.svg"
          width="30"
          height="30"
          className="d-inline-block align-top"
        />{' '}
        OTP Debug Client
      </Navbar.Brand>
      <LocationInputField location={tripQueryVariables.from} label="From" id="fromInputField" />
      <LocationInputField location={tripQueryVariables.to} label="To" id="toInputField" />
      <DepartureArrivalSelect tripQueryVariables={tripQueryVariables} setTripQueryVariables={setTripQueryVariables} />
      <TimeInputField tripQueryVariables={tripQueryVariables} setTripQueryVariables={setTripQueryVariables} />
      <DateInputField tripQueryVariables={tripQueryVariables} setTripQueryVariables={setTripQueryVariables} />
      <NumTripPatternsInput tripQueryVariables={tripQueryVariables} setTripQueryVariables={setTripQueryVariables} />
      <SearchWindowInput tripQueryVariables={tripQueryVariables} setTripQueryVariables={setTripQueryVariables} />
      <AccessSelect tripQueryVariables={tripQueryVariables} setTripQueryVariables={setTripQueryVariables} />
      <TransitModeSelect tripQueryVariables={tripQueryVariables} setTripQueryVariables={setTripQueryVariables} />
      <EgressSelect tripQueryVariables={tripQueryVariables} setTripQueryVariables={setTripQueryVariables} />
      <DirectModeSelect tripQueryVariables={tripQueryVariables} setTripQueryVariables={setTripQueryVariables} />
      <ItineraryFilterDebugSelect
        tripQueryVariables={tripQueryVariables}
        setTripQueryVariables={setTripQueryVariables}
      />
      <div className="search-bar-route-button-wrapper">
        <Button variant="primary" onClick={() => onRoute()}>
          Route
        </Button>
      </div>
    </div>
  );
}
