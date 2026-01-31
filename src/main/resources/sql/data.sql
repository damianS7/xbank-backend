-- Users
INSERT INTO public.user_accounts (email, password_hash, "role", status, created_at, updated_at) values
	 ('damian@demo.com', '$2a$10$hyxP/Azy1W1OjjhRarmDzO3J.CcMc5n1D4UzQJKUD4YD/yPV4AL06','ADMIN'::public."user_role_type", 'PENDING_VERIFICATION'::public."user_status_type",'2025-04-17 02:07:41.382291','2025-04-17 02:07:41.382291'),
	 ('alice@demo.com', '$2a$10$hyxP/Azy1W1OjjhRarmDzO3J.CcMc5n1D4UzQJKUD4YD/yPV4AL06','CUSTOMER'::public."user_role_type", 'PENDING_VERIFICATION'::public."user_status_type", '2025-04-06 02:42:27.378616','2025-04-06 02:42:27.378616');

-- Profile
INSERT INTO public.user_profiles (user_id,first_name,last_name,phone_number,birthdate,gender,photo,address,postal_code,country,national_id,updated_at) VALUES
	 (1,'Damian','White','701 444 113','1987-06-02','MALE'::public."user_gender_type", 'avatar.jpg','Apple AV.','50240','USA','723173823Z','2025-04-28 01:47:10.771533'),
	 (2,'Alice','Brown','901 322 223','1993-07-04','FEMALE'::public."user_gender_type", 'avatar.jpg','Lemon AV','30140','USA','01116613Z','2025-04-28 01:48:28.903419');

-- Settings
INSERT INTO public.user_settings (user_id, settings)
VALUES
    (1, '{
    "appNotifications": true,
    "emailNotifications": true,
    "multifactor": false,
    "signOperations": false,
    "signOperationsPIN": "0000",
    "sessionTimeout": 60,
    "multifactorMethod": "EMAIL",
    "language": "EN",
    "theme": "LIGHT"
  }'::jsonb),
    (2, '{
    "appNotifications": true,
    "emailNotifications": true,
    "multifactor": false,
    "signOperations": false,
    "signOperationsPIN": "0000",
    "sessionTimeout": 60,
    "multifactorMethod": "EMAIL",
    "language": "EN",
    "theme": "LIGHT"
  }'::jsonb);