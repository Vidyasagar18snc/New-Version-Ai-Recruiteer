<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <title>Offer Letter</title>
    <style>
        @page {
            size: A4;
            margin: 20mm 15mm 20mm 15mm;
            @bottom-center {
                content: counter(page);
                font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
                font-size: 10px;
                color: #64748b;
            }
        }

        body {
            font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
            color: #1e293b;
            line-height: 1.5;
            margin: 0;
            padding: 0;
        }

        .page {
            page-break-after: always;
        }

        .page:last-child {
            page-break-after: avoid;
        }

        /* Header Style */
        .company-header {
            margin-bottom: 20px;
        }

        .company-name {
            font-size: 20px;
            font-weight: bold;
            color: #0f172a;
            letter-spacing: 0.5px;
            text-transform: uppercase;
        }

        .office-info {
            font-size: 11px;
            color: #475569;
            margin-top: 5px;
            line-height: 1.4;
        }

        .divider {
            border-bottom: 1.5px solid #cbd5e1;
            margin: 15px 0;
        }

        /* Title Area */
        .doc-title-container {
            margin-bottom: 25px;
        }

        .doc-title {
            font-size: 22px;
            font-weight: bold;
            color: #0f172a;
            letter-spacing: 1px;
            text-transform: uppercase;
            margin-bottom: 10px;
        }

        .meta-info {
            font-size: 12px;
            color: #334155;
            line-height: 1.6;
        }

        .meta-label {
            font-weight: bold;
        }

        /* Section Headings */
        .section-title {
            font-size: 15px;
            font-weight: bold;
            color: #0f172a;
            margin-top: 25px;
            margin-bottom: 12px;
            text-transform: capitalize;
        }

        /* Table Layouts */
        .data-table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 20px;
        }

        .data-table th, .data-table td {
            border: 1px solid #cbd5e1;
            padding: 8px 12px;
            font-size: 12.5px;
            text-align: left;
        }

        .data-table th {
            background-color: #f8fafc;
            font-weight: bold;
            color: #334155;
        }

        .data-table td.key {
            font-weight: bold;
            color: #334155;
            background-color: #f8fafc;
            width: 35%;
        }

        .data-table td.val {
            color: #1e293b;
            width: 65%;
        }

        /* Subject and Body text */
        .subject-line {
            font-size: 14px;
            font-weight: bold;
            color: #0f172a;
            margin-top: 25px;
            margin-bottom: 15px;
        }

        .salutation {
            font-size: 13px;
            font-weight: bold;
            margin-bottom: 12px;
        }

        .body-text {
            font-size: 13px;
            color: #334155;
            margin-bottom: 15px;
            text-align: justify;
        }

        /* List Items styling */
        .benefits-list, .docs-list {
            padding-left: 20px;
            margin-bottom: 20px;
        }

        .benefits-list li, .docs-list li {
            font-size: 12.5px;
            color: #334155;
            margin-bottom: 5px;
        }

        .terms-list {
            padding-left: 20px;
            margin-bottom: 25px;
        }

        .terms-list li {
            font-size: 12.5px;
            color: #334155;
            margin-bottom: 8px;
            text-align: justify;
        }

        /* Signatures and Acceptance Area */
        .acceptance-section {
            border: 1px solid #e2e8f0;
            padding: 15px;
            border-radius: 6px;
            background-color: #fafbfc;
            margin-bottom: 25px;
        }

        .acceptance-title {
            font-size: 13.5px;
            font-weight: bold;
            margin-bottom: 10px;
            color: #0f172a;
        }

        .signature-line-row {
            margin-top: 20px;
        }

        .sig-placeholder-table {
            width: 100%;
            border-collapse: collapse;
        }

        .sig-placeholder-table td {
            border: none;
            padding: 5px 0;
            font-size: 12.5px;
            width: 33%;
        }

        .company-signature-area {
            margin-top: 25px;
            font-size: 13px;
            line-height: 1.5;
        }

        .company-sig-title {
            font-weight: bold;
            color: #0f172a;
        }

        .company-sig-name {
            font-size: 13px;
            font-weight: bold;
            color: #1e293b;
            margin-top: 5px;
        }

        .company-sig-dept {
            font-size: 12px;
            color: #64748b;
        }

        .disclaimer-section {
            margin-top: 30px;
            font-size: 10.5px;
            color: #64748b;
            border-top: 1px solid #e2e8f0;
            padding-top: 10px;
            line-height: 1.4;
        }
    </style>
</head>
<body>

<!-- ==================== PAGE 1 ==================== -->
<div class="page">
    <div class="company-header">
        <div class="company-name">${companyName!"HG INFOTECH PRIVATE LIMITED"}</div>
        <div class="office-info">
            <strong>Registered Office:</strong><br/>
            ${companyAddress!"HG Tech Park, Whitefield, Bengaluru - 560066, Karnataka, India"}<br/>
            Phone: ${hrPhone!"+91-XXXXXXXXXX"} | Email: ${hrEmail!"hr@hginfotech.com"} | Website: www.hginfotech.com
        </div>
    </div>

    <div class="divider"></div>

    <div class="doc-title-container">
        <div class="doc-title">OFFER LETTER</div>
        <div class="meta-info">
            <span class="meta-label">Offer Reference No.:</span> ${offerRefNo!"HG/HR/2026/OL-00125"}<br/>
            <span class="meta-label">Date:</span> ${formattedDate}
        </div>
    </div>

    <div class="divider"></div>

    <div class="subject-line">Subject: Offer of Employment</div>
    <div class="salutation">Dear ${name},</div>
    <div class="body-text">
        We are delighted to offer you the position of <strong>${role}</strong> at <strong>${companyName!"HG Infotech Private Limited"}</strong>.
    </div>
    <div class="body-text">
        Based on your qualifications and interview performance, we are confident that your skills and experience will contribute significantly to our organization's growth and success.
    </div>
    <div class="body-text">
        Your employment will commence on <strong>${formattedJoiningDate}</strong>, subject to successful completion of background verification and submission of the required documents.
    </div>

    <div class="divider"></div>

    <div class="section-title">Candidate Information</div>
    <table class="data-table">
        <tr>
            <td class="key">Candidate Name</td>
            <td class="val">${name}</td>
        </tr>
        <tr>
            <td class="key">Candidate ID</td>
            <td class="val">${candidateId!"CAN-2026-00125"}</td>
        </tr>
        <tr>
            <td class="key">Email Address</td>
            <td class="val">${email}</td>
        </tr>
        <tr>
            <td class="key">Mobile Number</td>
            <td class="val">${candidatePhone!"+91 XXXXXXXXXX"}</td>
        </tr>
        <tr>
            <td class="key">Position Offered</td>
            <td class="val">${role}</td>
        </tr>
        <tr>
            <td class="key">Department</td>
            <td class="val">${department!"Engineering"}</td>
        </tr>
        <tr>
            <td class="key">Location</td>
            <td class="val">${location!"Bengaluru"}</td>
        </tr>
        <tr>
            <td class="key">Reporting Manager</td>
            <td class="val">${reportingManager!"Engineering Manager"}</td>
        </tr>
        <tr>
            <td class="key">Employment Type</td>
            <td class="val">${employmentType!"Full-Time"}</td>
        </tr>
        <tr>
            <td class="key">Work Mode</td>
            <td class="val">Hybrid</td>
        </tr>
    </table>
</div>

<!-- ==================== PAGE 2 ==================== -->
<div class="page">
    <div class="section-title">Compensation Structure (Annual CTC)</div>
    <table class="data-table">
        <thead>
            <tr>
                <th>Earnings</th>
                <th>Annual (${currency!"₹"})</th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td>Basic Salary</td>
                <td>${salaryBasic?string["#,##,##0"]}</td>
            </tr>
            <tr>
                <td>House Rent Allowance (HRA)</td>
                <td>${salaryHra?string["#,##,##0"]}</td>
            </tr>
            <tr>
                <td>Special Allowance</td>
                <td>${salarySpecial?string["#,##,##0"]}</td>
            </tr>
            <tr>
                <td>Conveyance Allowance</td>
                <td>${salaryConveyance?string["#,##,##0"]}</td>
            </tr>
            <tr>
                <td>Medical Allowance</td>
                <td>${salaryMedical?string["#,##,##0"]}</td>
            </tr>
            <tr>
                <td>Performance Allowance</td>
                <td>${salaryPerformance?string["#,##,##0"]}</td>
            </tr>
            <tr>
                <td>Employer PF Contribution</td>
                <td>${salaryPf?string["#,##,##0"]}</td>
            </tr>
            <tr>
                <td>Gratuity</td>
                <td>${salaryGratuity?string["#,##,##0"]}</td>
            </tr>
            <tr style="font-weight: bold; background-color: #eff6ff; color: #1e3a8a;">
                <td>Total Annual CTC</td>
                <td>₹ ${salaryTotalCTC?string["#,##,##0"]}</td>
            </tr>
        </tbody>
    </table>

    <div class="divider"></div>

    <div class="section-title">Employment Terms</div>
    <table class="data-table">
        <tr>
            <td class="key">Date of joining</td>
            <td class="val">${formattedJoiningDate}</td>
        </tr>
        <tr>
            <td class="key">Probation Period</td>
            <td class="val">${probationPeriod!"6 Months"}</td>
        </tr>
        <tr>
            <td class="key">Notice Period</td>
            <td class="val">${noticePeriod!"30 Days"}</td>
        </tr>
        <tr>
            <td class="key">Working Hours</td>
            <td class="val">9 Hours Per Day</td>
        </tr>
        <tr>
            <td class="key">Working Days</td>
            <td class="val">Monday - Friday</td>
        </tr>
        <tr>
            <td class="key">Leave Policy</td>
            <td class="val">As per Company Policy</td>
        </tr>
    </table>

    <div class="divider"></div>

    <div class="section-title">Employee Benefits</div>
    <ul class="benefits-list">
        <li>Group Medical Insurance</li>
        <li>Provident Fund Benefits</li>
        <li>Gratuity Benefits</li>
        <li>Paid Annual Leave</li>
        <li>Learning &amp; Certification Support</li>
        <li>Employee Referral Program</li>
        <li>Performance Incentives</li>
        <li>Annual Performance Review</li>
    </ul>
</div>

<!-- ==================== PAGE 3 ==================== -->
<div class="page">
    <div class="section-title">Documents Required at Joining</div>
    <ul class="docs-list">
        <li>Aadhaar Card</li>
        <li>PAN Card</li>
        <li>Passport Size Photograph</li>
        <li>Educational Certificates</li>
        <li>Previous Employment Documents</li>
        <li>Last 3 Months Salary Slips</li>
        <li>Bank Account Details</li>
    </ul>

    <div class="divider"></div>

    <div class="section-title">Terms &amp; Conditions</div>
    <ol class="terms-list">
        <li>Employment is subject to successful background verification.</li>
        <li>All company information and intellectual property shall remain confidential.</li>
        <li>Employees must comply with all company policies and code of conduct.</li>
        <li>Company issued assets must be returned upon separation.</li>
        <li>This offer remains valid for <strong>7 calendar days</strong> from the date of issue.</li>
    </ol>

    <div class="divider"></div>

    <div class="acceptance-section">
        <div class="acceptance-title">Acceptance of Offer</div>
        <div class="body-text" style="margin-bottom: 15px;">
            I hereby accept the offer of employment and agree to abide by the terms and conditions mentioned above.
        </div>
        <div class="signature-line-row">
            <table class="sig-placeholder-table">
                <tr>
                    <td><strong>Candidate Signature:</strong> ____________</td>
                    <td><strong>Name:</strong> ______________________</td>
                    <td><strong>Date:</strong> ________________</td>
                </tr>
            </table>
        </div>
    </div>

    <div class="company-signature-area">
        <div class="company-sig-title">For ${companyName!"HG Infotech Private Limited"}</div>
        <div class="company-sig-name">${hrSignatoryName!"Rahul Menon"}</div>
        <div class="company-sig-dept">${hrSignatoryTitle!"HR Department"}</div>
        <div class="company-sig-dept">${companyName!"HG Infotech Private Limited"}</div>
    </div>

    <div class="disclaimer-section">
        <strong>Disclaimer:</strong> This document is electronically generated through the <strong>HG AI Recruit Platform</strong> and is valid without a physical signature when issued through the authorized HR system.
    </div>
</div>

</body>
</html>
