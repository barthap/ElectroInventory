-- This SQL file contains some example data
-- it is loaded when Spring starts in dev mode with embedded H2 db

INSERT INTO Categories (`id`, `name`, `parent_id`) VALUES
(11, 'Semiconductors', null),
(22, 'Transistors', 11),
(23, 'ICs', 11),
(33, 'Passive elements', null),
(34, 'Resistors', 33),
(35, 'Capacitors', 33),
(44, 'Light sources', null);

INSERT INTO Items (id, name, quantity, description, website, category_id) VALUES
(123, 'BC 327', 3, 'PNP Transistor', null, 22),
(124, 'IRLZ44N', 6, 'N-Channel MOSFET', null, 22),
(125, 'STM32F103', 2, 'ARM Microcontroller', 'https://st.com', 23),
(126, '100 Ohm 0.25W', 70, 'THT Resistor', null, 34),
(127, '100nF', 52, 'Ceramic capacitor', null, 35),
(128, 'ATmega8', 1, 'AVR Microcontroller DIP28', 'https://ww1.microchip.com/downloads/en/DeviceDoc/Microchip%208bit%20mcu%20AVR%20ATmega8A%20data%20sheet%2040001974A.pdf', 23),
(129, '1n4007', 10, 'Diode 1A', null, 11),
(130, 'LCD 2x16', 1, 'HD44780 driver, alphanumeric, blue', null, null),
(131, 'Blue LED', 3, 'LED 5mm, 20mA', null, 44),
(132, 'Yellow LED', 6, 'LED 5mm, 20mA', null, 44),
(133, 'Green LED', 5, 'LED 5mm, 20mA', null, 44);;