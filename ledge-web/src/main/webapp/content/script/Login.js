function loginError(status)
{
	var result = '';
			switch(status)
			{
			case 'account_blocked_1':
  				result = 'Konto nie zostało potwierdzone, potwierdz wysłany emailem link';
			break;
			case 'account_blocked_2':
  				result = 'Konto zostało zablokowane w wyniku jego nieaktywności';
			break;
			case 'account_blocked_3':
				result = 'Konto zostało zablokowane w wyniku wygaśnięcia hasła';
			break;
			case 'account_blocked_4':
				result = 'Konto zostało zablokowane przez admina z powodu naruszenia regulaminu';
			break;
			case 'account_blocked_5':
				result = 'Konto zostało zablokowane przez admina z powodu spamu';
			break;
			case 'account_blocked_6':
				result = 'Konto zostało zablokowane przez admina';
			break;
			case 'account_blocked_7':
				result = 'Konto zostało usunięte';
			break;
			case 'invalid_credentials': 
				result = 'Podany login lub hasło jest nieprawidłowe';
			break;
			case 'internal_error':
				result = 'Blad wewnetrzny - uzytkownik niezalogowany';
			break;
			default:
				result = 'Konto jest zablokowane z nieznanej przyczyny skontaktuj się z administratorem systemu';
			}
	return result;
}


















