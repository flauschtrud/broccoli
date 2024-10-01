import csv
import json

always_first = False
debug = False

print("""--------------------
Hint    The format of the calendar will have to be
Hint
Hint    [Calendars titles,.. or empty]
Hint    'Key', 'Translation', ['1' - '12'], [Empty],.. ['1' - '12']
Hint    [Keyname], [Translation], [Marker], [Empty],.. [Marker]
Hint
Hint    where multiple calendars are optional.
Hint    In case of multiple calendars you can select one option.
Hint
Hint    You can set the program to always take the first one in the code under `always_first`.
Hint
Hint    A marker can consist of
Hint      - X and x for full month and half month (! \/)
Hint        - ! Be careful that if there are full months, for the sake of simplification ALL small x will be eliminated
Hint      - Anything else for full month
--------------------""")

filename = input("Path to csv calendar file > ")
final_json = {
    "food": []
}

with open(filename, 'r') as file:
    reader = csv.reader(file)
    next(reader)

    calendar_count = next(reader).count('12')
    print(str(calendar_count) + ' calendar detected')

    for row in reader:
        # Get only calendars with values
        calendars = list(filter(lambda calendar: not all(
            string == '' or string.isspace() or string == '?' for string in calendar), [
            row[2 + i * (12 + 1): 2 + i + (i + 1) * 12] for i in range(calendar_count)
        ]))
        for i in range(len(calendars)):
            # Case 1: x x x X -> - - - X
            # Case 2: X x x x -> X - - -
            # Case 3:   x x   -> - X
            # We can be sure there is a real season and can erase the small x
            if 'X' not in calendars[i]:
                original_cal = calendars[i][:]
                for x in range(13):  # Go from 12 over 1 to 12
                    x -= 1

                    if x < 11 and calendars[i][x+1] == 'x' and original_cal[x] == 'x':
                        calendars[i][x+1] = 'X'
                    # Eliminate last small x in a row while not eliminating if it's the only one
                    elif (x > 0 and calendars[i][x-1] == 'X' or x == 0 and calendars[i][11] == 'X') and \
                        original_cal[x] == 'x' and \
                            (x < 11 and calendars[i][x+1] == '' or x == 11 and calendars[i][0] == ''):
                        calendars[i][x] = ''

            calendars[i] = list(
                map(lambda val: '' if val == 'x' else val, calendars[i]))

        if debug:
            print(row[0], calendars)
            input()

        if len(calendars) == 0:
            print(f"Skipping {row[0]}; empty")
            continue

        months = []

        for i in range(12):
            values = list(filter(lambda val: not (val == '' or val.isspace() or val == '?'),
                                 [calendar[i] for calendar in calendars]))

            if len(values) == 0:
                continue
            elif len(values) > 1 and not always_first:
                take = input(
                    f'We got {values} for {row[0]} in the {i+1}. month. Which one do you want to take [1 - {len(calendars)}] > ')
                take = 0 if take == '' else int(take) - 1
            else:
                take = 0

            if values[take] == '' or values[take].isspace() or values[take] == '?':
                continue
            else:
                months.append(i)

            if debug:
                print(months)

        final_json['food'].append({
            "name": row[0],
            "months": months
        })

        print(f"Added {row[0]} for {months}")
        if debug:
            input()

final_json = json.dumps(final_json, separators=(', ', ': '))

out = input(
    "\nGive the path to a JSON file or leave blank for output directly to the console > ")
if out == '':
    print(str(final_json))
else:
    with open(out, 'w') as output:
        output.write(final_json)

input("Finished. Press enter to exit")
