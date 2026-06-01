<!DOCTYPE html>
<html>
<head>
    <style>
        body {
            font-family: Arial;
            margin: 40px;
            color: #333;
            line-height: 1.6;
        }

        .header {
            display: flex;
            justify-content: space-between;
            border-bottom: 2px solid #2c5aa0;
            padding-bottom: 10px;
        }

        .logo {
            font-size: 22px;
            font-weight: bold;
            color: #2c5aa0;
        }

        .date {
            color: #666;
        }

        .section {
            margin-top: 25px;
        }

        .highlight {
            background: #f5f7fa;
            padding: 15px;
            border-left: 4px solid #2c5aa0;
            margin-top: 15px;
        }

        ul {
            margin-top: 10px;
        }

        .footer {
            margin-top: 40px;
        }

        .signature {
            font-weight: bold;
            margin-top: 30px;
        }

        .hr-box {
            margin-top: 20px;
            padding: 10px;
            background: #fafafa;
            border: 1px solid #ddd;
        }
    </style>
</head>

<body>

<!-- HEADER -->
<div class="header">
    <div class="logo">${companyName}</div>
    <div class="date">${date}</div>
</div>

<!-- CANDIDATE DETAILS -->
<div class="section">
    <p><b>${name}</b></p>
    <p>${address}</p>
</div>

<!-- GREETING -->
<div class="section">
    <p>Dear ${name},</p>
</div>

<!-- OFFER CONTENT -->
<div class="section">
    <p>
        We are pleased to offer you the position of
        <b>${role}</b> in the <b>${department}</b> department at
        <b>${companyName}</b>.
    </p>

    <div class="highlight">
        <p><b>Offer Details:</b></p>

        <ul>
            <li><b>Salary:</b> ₹${salary?string["#,##0"]}</li>
            <li><b>Joining Date:</b> ${joiningDate}</li>
            <li><b>Employment Type:</b> ${employmentType}</li>
            <li><b>Work Location:</b> ${location}</li>
        </ul>
    </div>

    <p><b>Benefits:</b></p>

    <ul>
        <li>Medical Insurance</li>
        <li>Paid Leaves</li>
        <li>Performance Bonus</li>
        <li>Flexible Work Environment</li>
    </ul>

    <p>
        Please confirm your acceptance of this offer within
        <b>7 days</b> from the date of this letter.
    </p>
</div>

<!-- HR CONTACT -->
<div class="hr-box">
    <p><b>For any queries, contact:</b></p>
    <p>Email: ${hrEmail}</p>
    <p>Phone: ${hrPhone}</p>
</div>

<!-- FOOTER -->
<div class="footer">
    <p>Sincerely,</p>

    <p class="signature">${hrSignatoryName}</p>
    <p>${hrSignatoryTitle}</p>

    <p><b>${companyName}</b></p>
    <p>${companyAddress}</p>
</div>

</body>
</html>